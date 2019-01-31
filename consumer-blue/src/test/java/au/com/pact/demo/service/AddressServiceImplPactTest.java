package au.com.pact.demo.service;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import au.com.pact.demo.model.AddressResponse;
import au.com.pact.demo.util.RestTemplateExecutor;
import groovy.json.JsonOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import scala.util.parsing.json.JSON;

import static au.com.pact.demo.constant.DefaultValues.CONSUMER_BLUE;
import static au.com.pact.demo.constant.DefaultValues.PROVIDER_LEMON;
import static au.com.pact.demo.util.RestTemplateExecutorBuilder.buildRestTemplate;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddressServiceImplPactTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Spy
    private RestTemplateExecutor restTemplate = buildRestTemplate("http://localhost:8082");

    // TODO: Create rule PactProviderRuleMk2 to enable mock provider
    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(PROVIDER_LEMON, "localhost", 8082, this);

    // TODO: Create Address Pact (Provider Lemon <--> Consumer Blue)
    @Pact(provider = PROVIDER_LEMON, consumer = CONSUMER_BLUE)
    public RequestResponsePact createAddressPact(PactDslWithProvider builder) {
        PactDslJsonBody expectedResponse = new PactDslJsonBody()
                .array("addresses")
                .stringType("1304/7 Riverside Quay, VIC 3006")
                .stringType("1305/8 Riverside Quay, VIC 3006")
                .closeArray()
                .asBody();

        return builder
                .uponReceiving("Search Addresses")
                .path("/addresses")
                .query("keyword=13 Riverside")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(expectedResponse)
                .toPact();
    }

    // TODO: Create Pact Test for Provider Lemon
    @Test
    @PactVerification(PROVIDER_LEMON)
    public void shouldSearchAddressesGivenAddressKeyword() {
        AddressResponse addressResponse = addressService.searchAddresses("13 Riverside");
        assertThat(addressResponse.getAddresses(), hasSize(2));
        assertThat(addressResponse.getAddresses().get(0), is("1304/7 Riverside Quay, VIC 3006"));
    }

}