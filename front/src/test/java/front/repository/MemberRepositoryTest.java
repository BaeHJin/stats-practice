package front.repository;

import front.FeignConfig;
import front.exception.InteractionException;
import front.exception.NotFoundUserException;
import lombok.val;
import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FeignConfig.class, MemberRepository.class})
@ImportAutoConfiguration(classes = {FeignAutoConfiguration.class, ValidationAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
//@EnableAutoConfiguration
// 실제 원격 테스트 하려면,
// -- 아래 주석을 해제하고
// -- com.github.tomakehurst.wiremock.client.WireMock.verify(RequestPatternBuilder) 부분을 주석처리 한다.
//@TestPropertySource(
//        properties = {
//                "client.mobile.host=https://m-apis-sdev.xk5.com"
//        }
//)
@AutoConfigureWireMock(port = 0)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository repository;

    @Test
    public void testGetCharacter() {

        //given
        val givenResponse = "{ \n" +
                "   \"return_code\":0,\n" +
                "   \"return_message\":\"Success\",\n" +
                "   \"member_no\":1000,\n" +
                "   \"game_id\":\"testGame\",\n" +
                "   \"character_id\":\"1234\",\n" +
                "   \"character_name\":\"NAME\",\n" +
                "   \"character_reg_dt\":1519199315000,\n" +
                "   \"country_cd\":\"KR\",\n" +
                "   \"provider_cd\":\"Email\"\n" +
                "}";


        stubFor(
                get(urlPathEqualTo("/member/v2/character/info/1234"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                        .withQueryParam("gameId", equalTo("testGame"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(givenResponse)
                        )
        );

        //when
        val actual = repository.getCharacter("testGame", "1234");

        //then
        assertThat(actual.getId().getCharacterNo()).isEqualTo("1234");

    }


    @Test(expected = NotFoundUserException.class)
    public void testGetCharacterInvalidCharacter() {

        //given
        val givenResponse = "{ \n" +
                "   \"return_code\":100,\n" +
                "   \"return_message\":\"Success\",\n" +
                "   \"member_no\":1000,\n" +
                "   \"game_id\":\"testGame\",\n" +
                "   \"character_id\":\"1234\",\n" +
                "   \"character_name\":\"NAME\",\n" +
                "   \"character_reg_dt\":1519199315000,\n" +
                "   \"country_cd\":\"KR\",\n" +
                "   \"provider_cd\":\"Email\"\n" +
                "}";

        stubFor(
                get(urlPathEqualTo("/member/v2/character/info/1234"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                        .withQueryParam("gameId", equalTo("testGame"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(givenResponse)
                        )
        );

        //when
        repository.getCharacter("testGame", "1234");

    }

    @Test(expected = InteractionException.class)
    public void testSaveInteractionFailure() {

        //given
        val givenResponse = "{ \n" +
                "   \"return_code\":500,\n" +
                "   \"return_message\":\"Error\"\n" +
                "}";

        stubFor(
                get(urlPathEqualTo("/member/v2/character/info/1234"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                        .withQueryParam("gameId", equalTo("testGame"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(givenResponse)
                        )
        );

        //when
        repository.getCharacter("testGame", "1234");
    }


    @Test
    public void testGetCharacterInfo() {

        //given
        val givenResponse = "{ \n" +
                "   \"return_code\":0,\n" +
                "   \"return_message\":\"Success\",\n" +
                "   \"member_no\":1000,\n" +
                "   \"game_id\":\"testGame\",\n" +
                "   \"character_id\":\"1234\",\n" +
                "   \"character_name\":\"NAME\",\n" +
                "   \"world_id\": null,\n" +
                "   \"character_reg_dt\":1519199315000,\n" +
                "   \"country_cd\":\"KR\",\n" +
                "   \"provider_cd\":\"Email\"\n" +
                "}";


        stubFor(
                get(urlPathEqualTo("/member/v2/character/info/1234"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                        .withQueryParam("gameId", equalTo("testGame"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(givenResponse)
                        )
        );

        val expected = new MemberRepository.CharacterInfoResponse(0L, "Success", "1000", "testGame",
                "1234", "NAME", null, 1519199315000L, "KR", "Email");

        //when
        val actual = repository.getCharacterInfo("testGame", "1234");

        //then
        assertThat(actual).isEqualTo(expected);

    }

    @Test(expected = InteractionException.class)
    public void testGetCharacterInfoFileGetInfraToken() {

        //given
        val givenResponse = "{ \n" +
                "   \"return_code\":0,\n" +
                "   \"return_message\":\"Success\",\n" +
                "   \"member_no\":1000,\n" +
                "   \"game_id\":\"testGame\",\n" +
                "   \"character_id\":\"1234\",\n" +
                "   \"character_name\":\"NAME\",\n" +
                "   \"world_id\": null,\n" +
                "   \"character_reg_dt\":1519199315000,\n" +
                "   \"country_cd\":\"KR\",\n" +
                "   \"provider_cd\":\"Email\"\n" +
                "}";


        stubFor(
                get(urlPathEqualTo("/member/v2/character/info/1234"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                        .withQueryParam("gameId", equalTo("testGame"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(givenResponse)
                        )
        );

        //when
        repository.getCharacterInfo("testGame", "1234");

        //then

    }

    @Test(expected = InteractionException.class)
    public void testGetCharacterInfoInteractionException() {

        //given


        stubFor(
                get(urlPathEqualTo("/member/v2/character/info/1234"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                        .withQueryParam("gameId", equalTo("testGame"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody("")
                        )
        );

        //when
        repository.getCharacterInfo("testGame", "1234");

        //then

    }


}
