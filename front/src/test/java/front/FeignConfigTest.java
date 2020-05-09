package front;


import feign.RetryableException;
import lombok.val;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FeignConfig.class})
@ImportAutoConfiguration(classes = {FeignAutoConfiguration.class, ValidationAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
        properties = {
                "feign.client.config.test.connectTimeout=500",
                "feign.client.config.test.readTimeout=500",
        }
)
public class FeignConfigTest {

    @Autowired
    private TestClient testClient;

    @Before
    public void before() {

        stubFor(
                get(urlEqualTo("/"))
                        .willReturn(
                                aResponse()
                                        .withBody("OK")
                        )
        );

        stubFor(
                get(urlEqualTo("/timeout-test"))
                        .willReturn(
                                aResponse()
                                        .withFixedDelay(500)
                        )
        );

    }

    @Test
    @Ignore
    public void testCall() {

        // given

        // when
        val actual = testClient.call();

        // then
        assertThat(actual).isEqualTo("OK");

    }

    @Test(expected = RetryableException.class)
    @Ignore
    public void testCall_timeout() {

        // given

        // when
        testClient.callTimeout();

    }


    @FeignClient(value = "test", url = "http://localhost:${wiremock.server.port}")
    interface TestClient {

        @GetMapping(
                value = {
                        "/"
                },
                produces = MediaType.TEXT_PLAIN_VALUE
        )
        String call();

        @GetMapping(
                value = {
                        "/timeout-test"
                },
                produces = MediaType.TEXT_PLAIN_VALUE
        )
        String callTimeout();

    }

}