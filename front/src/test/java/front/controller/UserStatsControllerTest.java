package front.controller;

import core.data.type.PermissionType;
import core.exception.NotFoundDataException;
import core.model.PagedGenericModel;
import front.data.type.UserType;
import front.data.Header;
import front.data.User;
import front.exception.NotFoundUserException;
import front.exception.StatsExceptionHandler;
import front.service.UserStatsService;
import lombok.val;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserStatsControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    private RestDocumentationResultHandler document;
    private MockMvc mockMvc;
    private UserStatsService service;
    private Header header;

    @Before
    public void before() {

        service = mock(UserStatsService.class);
        header = mock(Header.class);

        this.document = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint())
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserStatsController(service, header
                ))
                .setControllerAdvice(new StatsExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(print())
                .alwaysDo(document)
                .build();

    }

    @Test
    public void testUpdateCharacterStats() throws Exception {

        //given
        val givenParamUserId = "100";
        val givenStatsId = "TEST_STATS";
        val givenServiceId = "testGame";
        val givenUpdateDate = System.currentTimeMillis();

        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, givenUpdateDate);

        when(header.getGameId()).thenReturn("testGame");
        when(service.put(givenParamUserId, givenStatsId, givenServiceId, 10, UserType.S, PermissionType.CLIENT)).thenReturn(givenUserStats);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.post("/external/character/{character_no}/{stats_name}", "100", "TEST_STATS")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content("{  \n" +
                                        "\"value\":10\n" +
                                        "}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("character_no").description("캐릭터 번호"),
                                parameterWithName("stats_name").description("스텟 이름")
                        ),
                        requestFields(
                                fieldWithPath("value").description("값")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.stats_name").description("스텟 이름"),
                                fieldWithPath("result.updated_date").description(" 업데이트 날짜"),
                                fieldWithPath("result.value").description("값")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.stats_name").value("TEST_STATS"))
                .andExpect(jsonPath("$.result.updated_date").value(givenUpdateDate))
                .andExpect(jsonPath("$.result.value").value(10))
        ;
    }

    @Test
    public void testUpdateCharacterStats_case_StatsException() throws Exception {

        //given
        val givenParamUserId = "100";
        val givenStatsId = "TEST_STATS";
        val givenServiceId = "testGame";
        val givenUpdateDate = System.currentTimeMillis();


        when(header.getGameId()).thenReturn("testGame");
        when(service.put(givenParamUserId, givenStatsId, givenServiceId, 10, UserType.S, PermissionType.CLIENT)).thenThrow(new NotFoundUserException("not found user"));

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.post("/external/character/{character_no}/{stats_name}", "100", "TEST_STATS")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content("{  \n" +
                                        "\"value\":10\n" +
                                        "}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("character_no").description("캐릭터 번호"),
                                parameterWithName("stats_name").description("스텟 이름")
                        ),
                        requestFields(
                                fieldWithPath("value").description("값")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(405000))
                .andExpect(jsonPath("$.message").value("not found user"))
                .andExpect(jsonPath("$.result").isEmpty())
        ;
    }


    @Test
    public void testUpdateMemberStats() throws Exception {

        //given
        val givenParamUserId = "100";
        val givenStatsId = "TEST_STATS";
        val givenServiceId = "testGame";
        val givenUpdateDate = System.currentTimeMillis();

        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, givenUpdateDate);

        when(header.getGameId()).thenReturn("testGame");
        when(header.getMemberNo()).thenReturn("100");
        when(service.put(givenParamUserId, givenStatsId, givenServiceId, 10, UserType.M, PermissionType.CLIENT)).thenReturn(givenUserStats);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.post("/external/member/{member_no}/{stats_name}", "100", "TEST_STATS")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content("{  \n" +
                                        "\"value\":10\n" +
                                        "}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("member_no").description("회원 번호"),
                                parameterWithName("stats_name").description("스텟 이름")
                        ),
                        requestFields(
                                fieldWithPath("value").description("값")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.stats_name").description("스텟 이름"),
                                fieldWithPath("result.updated_date").description(" 업데이트 날짜"),
                                fieldWithPath("result.value").description("값")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.stats_name").value("TEST_STATS"))
                .andExpect(jsonPath("$.result.updated_date").value(givenUpdateDate))
                .andExpect(jsonPath("$.result.value").value(10))
        ;
    }


    @Test
    public void testGetCharacterStats() throws Exception {

        //given
        val givenParamUserId = "100";
        val givenStatsId = "TEST_STATS";
        val givenServiceId = "testGame";
        val givenUpdateDate = System.currentTimeMillis();

        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, givenUpdateDate);

        when(header.getGameId()).thenReturn("testGame");
        when(service.get(givenParamUserId, givenStatsId, givenServiceId, UserType.S)).thenReturn(givenUserStats);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.get("/external/character/{characterNo}/{stats_name}", "100","TEST_STATS")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("characterNo").description("character Number"),
                                parameterWithName("stats_name").description("스텟이름")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.stats_name").description("스텟 이름"),
                                fieldWithPath("result.updated_date").description(" 업데이트 날짜"),
                                fieldWithPath("result.value").description("값")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.stats_name").value("TEST_STATS"))
                .andExpect(jsonPath("$.result.updated_date").value(givenUpdateDate))
                .andExpect(jsonPath("$.result.value").value(10))
        ;
    }

    @Test
    public void testGetMemberStats() throws Exception {

        //given
        val givenParamUserId = "100";
        val givenStatsId = "TEST_STATS";
        val givenServiceId = "testGame";
        val givenUpdateDate = System.currentTimeMillis();

        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, givenUpdateDate);

        when(header.getGameId()).thenReturn("testGame");
        when(header.getMemberNo()).thenReturn("100");
        when(service.get(givenParamUserId, givenStatsId, givenServiceId, UserType.M)).thenReturn(givenUserStats);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.get("/external/member/{memberNo}/{stats_name}", "100", "TEST_STATS")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("memberNo").description("member Number"),
                                parameterWithName("stats_name").description("스텟이름")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.stats_name").description("스텟 이름"),
                                fieldWithPath("result.updated_date").description(" 업데이트 날짜"),
                                fieldWithPath("result.value").description("값")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.stats_name").value("TEST_STATS"))
                .andExpect(jsonPath("$.result.updated_date").value(givenUpdateDate))
                .andExpect(jsonPath("$.result.value").value(10))
        ;
    }



    @Test
    public void testGetCharacterStatsList() throws Exception {

        //given
        Pageable pageable = PageRequest.of(0, 10);

        val user = new User(new User.UserId(UserType.S, "101", "testGame", null, "123"));
        val user2 = new User(new User.UserId(UserType.S, "102", "testGame", null, "456"));

        user.getId().setId(null);
        user2.getId().setId(null);


        val userStats1 = Lists.newArrayList(
                core.service.userstats.UserStatsService.SearchUserStats.UserStats.builder()
                        .statsId("testStats")
                        .value(5L)
                        .updatedDate(1570753488000L)
                        .build()
        );

        val userStats2 = Lists.newArrayList(
                core.service.userstats.UserStatsService.SearchUserStats.UserStats.builder()
                        .statsId("testStats")
                        .value(4L)
                        .updatedDate(1570753488000L)
                        .build()
        );

        val expectedUserStats = Lists.newArrayList(
                UserStatsService.SearchUserStatsResponse.of(user, userStats1),
                UserStatsService.SearchUserStatsResponse.of(user2, userStats2)
        );

        val expected = PagedGenericModel.of(expectedUserStats, 1L, 2L, 1, 10);

        val requestUserIds = Lists.newArrayList(user.getId(), user2.getId());
        when(service.get(requestUserIds, pageable)).thenReturn(expected);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.post("/internal/user/list")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content("{  \n" +
                                        "\"users\":[" +
                                        "{" +
                                        "\"user_type\":\"S\",\n" +
                                        "\"service_id\":\"testGame\",\n" +
                                        "\"member_no\":\"101\",\n" +
                                        "\"world_id\":null,\n" +
                                        "\"character_no\":\"123\"\n" +
                                        "},\n" +
                                        "{" +
                                        "\"user_type\":\"S\",\n" +
                                        "\"service_id\":\"testGame\",\n" +
                                        "\"member_no\":\"102\",\n" +
                                        "\"world_id\":null,\n" +
                                        "\"character_no\":\"456\"\n" +
                                        "}" +
                                        "],\n" +
                                        "\"page\":0,\n" +
                                        "\"size\":10" +
                                        "}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("users").description("조회 할 유저 정보"),
                                fieldWithPath("users.[].user_type").description("S - 스토브 캐릭터\n" + "M - 스토브 맴버"),
                                fieldWithPath("users.[].service_id").description("서비스, 게임 아이디"),
                                fieldWithPath("users.[].member_no").description("스토브 회원 번호"),
                                fieldWithPath("users.[].world_id").description("월드 아이디").optional(),
                                fieldWithPath("users.[].character_no").description("userType이 \"S\"일 경우 필수\n" + "캐릭터번호"),
                                fieldWithPath("page").description("page number"),
                                fieldWithPath("size").description("content number per page")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.content").description("결과 내용"),
                                fieldWithPath("result.content.[].member_no").description("회원 번호"),
                                fieldWithPath("result.content.[].service_id").description("서비스 / 게임 아이디"),
                                fieldWithPath("result.content.[].character_no").description("캐릭터번호").optional(),
                                fieldWithPath("result.content.[].user_stats").description("유저 스텟 정보"),
                                fieldWithPath("result.content.[].user_stats.[].stats_name").description("스텟 이름"),
                                fieldWithPath("result.content.[].user_stats.[].value").description("값"),
                                fieldWithPath("result.content.[].user_stats.[].updated_date").description("최근 업데이트된 날짜"),
                                fieldWithPath("result.total_pages").description("전체 페이지 수"),
                                fieldWithPath("result.total_elements").description("전체 컨텐츠 수"),
                                fieldWithPath("result.page").description("요청한 페이지 번호 (zero based index)"),
                                fieldWithPath("result.size").description("페이지 당 컨텐츠 노출 수")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.content.[0].member_no").value("101"))
                .andExpect(jsonPath("$.result.content.[0].service_id").value("testGame"))
                .andExpect(jsonPath("$.result.content.[0].character_no").value("123"))
                .andExpect(jsonPath("$.result.content.[0].user_stats.[0].stats_name").value("testStats"))
                .andExpect(jsonPath("$.result.content.[0].user_stats.[0].value").value(5))
                .andExpect(jsonPath("$.result.content.[0].user_stats.[0].updated_date").value(1570753488000L))
                .andExpect(jsonPath("$.result.total_pages").value(1))
                .andExpect(jsonPath("$.result.total_elements").value(2))
                .andExpect(jsonPath("$.result.page").value(1))
                .andExpect(jsonPath("$.result.size").value(10))
        ;

    }



}
