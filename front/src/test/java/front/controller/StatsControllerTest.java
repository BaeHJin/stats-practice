package front.controller;

import com.google.common.collect.Sets;
import core.data.Stats;
import core.data.type.PermissionType;
import core.data.type.StatusType;
import core.data.type.UpdateType;
import core.data.type.ValueType;
import core.model.PagedGenericModel;
import core.service.stats.StatsService;
import front.exception.StatsExceptionHandler;
import lombok.val;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatsControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    private RestDocumentationResultHandler document;
    private MockMvc mockMvc;
    private StatsService service;

    @Before
    public void before() {

        service = mock(StatsService.class);

        this.document = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint())
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(new StatsController(service))
                .setControllerAdvice(new StatsExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(print())
                .alwaysDo(document)
                .build();

    }

    @Test
    public void testPut() throws Exception {

        //given
        val expected =  StatsService.StatsResult.builder().statsName("testStats")
                .serviceId("testService")
                .statsId("testService|testStats")
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(1570508918000L).updatedDate(1570508918000L)
                .build();

        when(service.insert(any(Stats.class))).thenReturn(expected);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.put("/internal/metadata/create")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content("{\n" +
                                        "  \"serviceId\": \"testService\",\n" +
                                        "  \"statsName\": \"testStats\",\n" +
                                        "  \"description\": \"test\",\n" +
                                        "  \"adminId\": \"test@test.com\",\n" +
                                        "  \"valueType\": \"INT\",\n" +
                                        "  \"permissionType\": \"ALL\",\n" +
                                        "  \"updateType\": \"INCREMENT\",\n" +
                                        "  \"maxChangeValue\": 100,\n" +
                                        "  \"minChangeValue\": 0,\n" +
                                        "  \"defaultValue\": 0,\n" +
                                        "  \"minValue\": 0,\n" +
                                        "  \"maxValue\": 100,\n" +
                                        "  \"status\": \"READY\"\n" +
                                        "}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("serviceId").description("서비스 아이디 / game_id"),
                                fieldWithPath("statsName").description("스텟 이름"),
                                fieldWithPath("description").description("스텟 설명"),
                                fieldWithPath("adminId").description("등록자 아이디"),
                                fieldWithPath("valueType").description("유저 스탯에 저장될 값의 타입 - INT(정수) / DOUBLE(소수점가능)"),
                                fieldWithPath("permissionType").description("유저 스텟을 사용할 수 있는 접근 허용 타입 - CLIENT / SERVER / ALL"),
                                fieldWithPath("updateType").description("\tupdateTypestring\n" +
                                        "example: INCREMENT\n" +
                                        "유저 스텟을 업데이트 하는 방법에 따른 타입\n" +
                                        "a. INCREMENT - 증가\n" +
                                        "b. REPLACE - 대체\n" +
                                        "c. MAX - 기존 값보다 클때만 대체\n" +
                                        "d. MIN - 기존 값보다 작을때만 대체"),
                                fieldWithPath("maxChangeValue").description("최대 변화량"),
                                fieldWithPath("minChangeValue").description("최소 변화량"),
                                fieldWithPath("defaultValue").description("기본값"),
                                fieldWithPath("minValue").description("최소값"),
                                fieldWithPath("maxValue").description("최대값"),
                                fieldWithPath("status").description("현재 상태")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.statsId").description("스텟 아이디"),
                                fieldWithPath("result.serviceId").description("서비스 아이디 / game_id"),
                                fieldWithPath("result.statsName").description(" 스텟 이름"),
                                fieldWithPath("result.description").description("스텟설명"),
                                fieldWithPath("result.regId").description(" 등록자 id"),
                                fieldWithPath("result.valueType").description("유저 스탯에 저장될 값의 타입 - INT(정수) / DOUBLE(소수점가능)"),
                                fieldWithPath("result.permissionType").description(" 유저 스텟을 사용할 수 있는 접근 허용 타입 - CLIENT / SERVER / ALL"),
                                fieldWithPath("result.updateType").description("\tupdateTypestring\n" +
                                        "example: INCREMENT\n" +
                                        "유저 스텟을 업데이트 하는 방법에 따른 타입\n" +
                                        "a. INCREMENT - 증가\n" +
                                        "b. REPLACE - 대체\n" +
                                        "c. MAX - 기존 값보다 클때만 대체\n" +
                                        "d. MIN - 기존 값보다 작을때만 대체"),
                                fieldWithPath("result.status").description(" 현재 상태"),
                                fieldWithPath("result.maxChangeValue").description("최대 변화량"),
                                fieldWithPath("result.minChangeValue").description("최소 변화량"),
                                fieldWithPath("result.minValue").description("최소값"),
                                fieldWithPath("result.maxValue").description("최대값"),
                                fieldWithPath("result.defaultValue").description("초기값"),
                                fieldWithPath("result.uptId").description("마지막 수정자 id"),
                                fieldWithPath("result.createdDate").description("생성 날짜"),
                                fieldWithPath("result.updatedDate").description("업데이트 날짜")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.statsId").value("testService|testStats"))
                .andExpect(jsonPath("$.result.serviceId").value("testService"))
                .andExpect(jsonPath("$.result.statsName").value("testStats"))
                .andExpect(jsonPath("$.result.description").value("des"))
                .andExpect(jsonPath("$.result.valueType").value("INT"))
                .andExpect(jsonPath("$.result.permissionType").value("ALL"))
                .andExpect(jsonPath("$.result.updateType").value("INCREMENT"))
                .andExpect(jsonPath("$.result.maxChangeValue").value(100L))
                .andExpect(jsonPath("$.result.minChangeValue").value(0L))
                .andExpect(jsonPath("$.result.minValue").value(0L))
                .andExpect(jsonPath("$.result.maxValue").value(100L))
                .andExpect(jsonPath("$.result.defaultValue").value(0L))
                .andExpect(jsonPath("$.result.status").value("READY"))
                .andExpect(jsonPath("$.result.uptId").value("test@test.com"))
                .andExpect(jsonPath("$.result.regId").value("test@test.com"))
                .andExpect(jsonPath("$.result.createdDate").exists())
                .andExpect(jsonPath("$.result.updatedDate").exists())
        ;
    }

    @Test
    public void testUpdate() throws Exception {

        //given
        val expected =  StatsService.StatsResult.builder().statsName("testStats")
                .serviceId("testService")
                .statsId("testService|testStats")
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(1570508918000L).updatedDate(1570508918000L)
                .build();

        when(service.update(any(Stats.class))).thenReturn(expected);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.put("/internal/metadata/update")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content("{\n" +
                                        "  \"serviceId\": \"testService\",\n" +
                                        "  \"statsName\": \"testStats\",\n" +
                                        "  \"description\": \"test\",\n" +
                                        "  \"adminId\": \"test@test.com\",\n" +
                                        "  \"valueType\": \"INT\",\n" +
                                        "  \"permissionType\": \"ALL\",\n" +
                                        "  \"updateType\": \"INCREMENT\",\n" +
                                        "  \"maxChangeValue\": 100,\n" +
                                        "  \"minChangeValue\": 0,\n" +
                                        "  \"defaultValue\": 0,\n" +
                                        "  \"minValue\": 0,\n" +
                                        "  \"maxValue\": 100,\n" +
                                        "  \"status\": \"READY\"\n" +
                                        "}")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestFields(
                                fieldWithPath("serviceId").description("서비스 아이디 / game_id"),
                                fieldWithPath("statsName").description("스텟 이름"),
                                fieldWithPath("description").description("스텟 설명"),
                                fieldWithPath("adminId").description("등록자 아이디"),
                                fieldWithPath("valueType").description("유저 스탯에 저장될 값의 타입 - INT(정수) / DOUBLE(소수점가능)"),
                                fieldWithPath("permissionType").description("유저 스텟을 사용할 수 있는 접근 허용 타입 - CLIENT / SERVER / ALL"),
                                fieldWithPath("updateType").description("\tupdateTypestring\n" +
                                        "example: INCREMENT\n" +
                                        "유저 스텟을 업데이트 하는 방법에 따른 타입\n" +
                                        "a. INCREMENT - 증가\n" +
                                        "b. REPLACE - 대체\n" +
                                        "c. MAX - 기존 값보다 클때만 대체\n" +
                                        "d. MIN - 기존 값보다 작을때만 대체"),
                                fieldWithPath("maxChangeValue").description("최대 변화량"),
                                fieldWithPath("minChangeValue").description("최소 변화량"),
                                fieldWithPath("defaultValue").description("기본값"),
                                fieldWithPath("minValue").description("최소값"),
                                fieldWithPath("maxValue").description("최대값"),
                                fieldWithPath("status").description("메타데이터 상태 타입\n" +
                                        "전체타입 : null\n" +
                                        "\n" +
                                        "대기 : READY\n" +
                                        "\n" +
                                        "사용 : OK\n" +
                                        "\n" +
                                        "만료 : EXPIRED")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.statsId").description("스텟 아이디"),
                                fieldWithPath("result.serviceId").description("서비스 아이디 / game_id"),
                                fieldWithPath("result.statsName").description(" 스텟 이름"),
                                fieldWithPath("result.description").description("스텟설명"),
                                fieldWithPath("result.regId").description(" 등록자 id"),
                                fieldWithPath("result.valueType").description("유저 스탯에 저장될 값의 타입 - INT(정수) / DOUBLE(소수점가능)"),
                                fieldWithPath("result.permissionType").description(" 유저 스텟을 사용할 수 있는 접근 허용 타입 - CLIENT / SERVER / ALL"),
                                fieldWithPath("result.updateType").description("\tupdateTypestring\n" +
                                        "example: INCREMENT\n" +
                                        "유저 스텟을 업데이트 하는 방법에 따른 타입\n" +
                                        "a. INCREMENT - 증가\n" +
                                        "b. REPLACE - 대체\n" +
                                        "c. MAX - 기존 값보다 클때만 대체\n" +
                                        "d. MIN - 기존 값보다 작을때만 대체"),
                                fieldWithPath("result.status").description(" 현재 상태"),
                                fieldWithPath("result.maxChangeValue").description("최대 변화량"),
                                fieldWithPath("result.minChangeValue").description("최소 변화량"),
                                fieldWithPath("result.minValue").description("최소값"),
                                fieldWithPath("result.maxValue").description("최대값"),
                                fieldWithPath("result.defaultValue").description("초기값"),
                                fieldWithPath("result.uptId").description("마지막 수정자 id"),
                                fieldWithPath("result.createdDate").description("생성 날짜"),
                                fieldWithPath("result.updatedDate").description("업데이트 날짜")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.statsId").value("testService|testStats"))
                .andExpect(jsonPath("$.result.serviceId").value("testService"))
                .andExpect(jsonPath("$.result.statsName").value("testStats"))
                .andExpect(jsonPath("$.result.description").value("des"))
                .andExpect(jsonPath("$.result.valueType").value("INT"))
                .andExpect(jsonPath("$.result.permissionType").value("ALL"))
                .andExpect(jsonPath("$.result.updateType").value("INCREMENT"))
                .andExpect(jsonPath("$.result.maxChangeValue").value(100L))
                .andExpect(jsonPath("$.result.minChangeValue").value(0L))
                .andExpect(jsonPath("$.result.minValue").value(0L))
                .andExpect(jsonPath("$.result.maxValue").value(100L))
                .andExpect(jsonPath("$.result.defaultValue").value(0L))
                .andExpect(jsonPath("$.result.status").value("READY"))
                .andExpect(jsonPath("$.result.uptId").value("test@test.com"))
                .andExpect(jsonPath("$.result.regId").value("test@test.com"))
                .andExpect(jsonPath("$.result.createdDate").exists())
                .andExpect(jsonPath("$.result.updatedDate").exists())
        ;
    }


    @Test
    public void testDelete() throws Exception {

        //given
        val givenServiceId = "testServiceId";
        val givenStatsNames = Sets.newHashSet("stats1", "stats2");

        when(service.delete(givenServiceId, givenStatsNames)).thenReturn(givenStatsNames);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.post("/internal/metadata/delete")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .param("serviceId","testServiceId")
                                .param("statsName", "stats1, stats2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("serviceId").description("service Id"),
                                parameterWithName("statsName").description("삭제할 스텟 이름")
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.[0]").value("stats2"))
                .andExpect(jsonPath("$.result.[1]").value("stats1"))
        ;
    }



    @Test
    public void testGetList() throws Exception {

        //given
        val givenServiceId = "testServiceId";
        val givenStatsName= "stats";
        val pageable = PageRequest.of(0, 10);

        val givenStats =  Lists.newArrayList(
                StatsService.StatsResult.builder()
                        .statsId("testService|testStats")
                        .statsName("testStats")
                        .serviceId("testService")
                        .description("des")
                        .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                        .maxChangeValue(100L).minChangeValue(0L)
                        .minValue(0L).maxValue(100L).defaultValue(0L)
                        .regId("test@test.com").uptId("test@test.com")
                        .createdDate(1570508918000L).updatedDate(1570508918000L)
                        .build(),
                StatsService.StatsResult.builder()
                        .statsId("testService|testStats2")
                        .statsName("testStats2")
                        .serviceId("testService")
                        .description("des")
                        .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                        .maxChangeValue(100L).minChangeValue(0L)
                        .minValue(0L).maxValue(100L).defaultValue(0L)
                        .regId("test@test.com").uptId("test@test.com")
                        .createdDate(1570508918000L).updatedDate(1570508918000L)
                        .build(),
                StatsService.StatsResult.builder()
                        .statsId("testService|testStats3")
                        .statsName("testStats3")
                        .serviceId("testService")
                        .description("des")
                        .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                        .maxChangeValue(100L).minChangeValue(0L)
                        .minValue(0L).maxValue(100L).defaultValue(0L)
                        .regId("test@test.com").uptId("test@test.com")
                        .createdDate(1570508918000L).updatedDate(1570508918000L)
                        .build()
        );

        val  expected = PagedGenericModel.of(givenStats, 1, 3, 0, 10);

        when(service.get(givenServiceId, givenStatsName,null,null, pageable)).thenReturn(expected);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.get("/internal/metadata/list")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .param("serviceId","testServiceId")
                                .param("statsName", "stats")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("serviceId").description("service Id"),
                                parameterWithName("statsName").description("스텟 이름").optional(),
                                parameterWithName("status").description("메타데이터 상태 타입\n" +
                                        "전체타입 : null\n" +
                                        "\n" +
                                        "대기 : READY\n" +
                                        "\n" +
                                        "사용 : OK\n" +
                                        "\n" +
                                        "만료 : EXPIRED").optional(),
                                parameterWithName("title").description("타이틀").optional(),
                                parameterWithName("page").description("\t\n" +
                                        "page number\n" +
                                        "\n" +
                                        "default value : 0").optional(),
                                parameterWithName("size").description("content number per page\n" +
                                        "\n" +
                                        "default value : 20").optional(),
                                parameterWithName("sort").description("sort order for content\n" +
                                        "\n" +
                                        "default : UNSORTED\n" +
                                        "\n" +
                                        "=> createdDate or updatedDate, DESC or ASC").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과"),
                                fieldWithPath("result.content").description("결과 내용"),
                                fieldWithPath("result.content.[].statsId").description("스텟 아이디"),
                                fieldWithPath("result.content.[].serviceId").description("서비스 아이디 / game_id"),
                                fieldWithPath("result.content.[].statsName").description(" 스텟 이름"),
                                fieldWithPath("result.content.[].description").description("스텟설명"),
                                fieldWithPath("result.content.[].regId").description(" 등록자 id"),
                                fieldWithPath("result.content.[].valueType").description("유저 스탯에 저장될 값의 타입 - INT(정수) / DOUBLE(소수점가능)"),
                                fieldWithPath("result.content.[].permissionType").description(" 유저 스텟을 사용할 수 있는 접근 허용 타입 - CLIENT / SERVER / ALL"),
                                fieldWithPath("result.content.[].updateType").description("\tupdateTypestring\n" +
                                        "example: INCREMENT\n" +
                                        "유저 스텟을 업데이트 하는 방법에 따른 타입\n" +
                                        "a. INCREMENT - 증가\n" +
                                        "b. REPLACE - 대체\n" +
                                        "c. MAX - 기존 값보다 클때만 대체\n" +
                                        "d. MIN - 기존 값보다 작을때만 대체"),
                                fieldWithPath("result.content.[].status").description(" 현재 상태"),
                                fieldWithPath("result.content.[].maxChangeValue").description("최대 변화량"),
                                fieldWithPath("result.content.[].minChangeValue").description("최소 변화량"),
                                fieldWithPath("result.content.[].minValue").description("최소값"),
                                fieldWithPath("result.content.[].maxValue").description("최대값"),
                                fieldWithPath("result.content.[].defaultValue").description("초기값"),
                                fieldWithPath("result.content.[].uptId").description("마지막 수정자 id"),
                                fieldWithPath("result.content.[].createdDate").description("생성 날짜"),
                                fieldWithPath("result.content.[].updatedDate").description("업데이트 날짜"),
                                fieldWithPath("result.total_pages").description("전체 페이지 수"),
                                fieldWithPath("result.total_elements").description("전체 컨텐츠 수\n"),
                                fieldWithPath("result.page").description("요청한 페이지 번호 (zero based index)"),
                                fieldWithPath("result.size").description("페이지 당 컨텐츠 노출 수")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.content.[0].statsId").value("testService|testStats"))
                .andExpect(jsonPath("$.result.content.[0].serviceId").value("testService"))
                .andExpect(jsonPath("$.result.content.[0].statsName").value("testStats"))
                .andExpect(jsonPath("$.result.content.[0].description").value("des"))
                .andExpect(jsonPath("$.result.content.[0].valueType").value("INT"))
                .andExpect(jsonPath("$.result.content.[0].permissionType").value("ALL"))
                .andExpect(jsonPath("$.result.content.[0].updateType").value("INCREMENT"))
                .andExpect(jsonPath("$.result.content.[0].maxChangeValue").value(100L))
                .andExpect(jsonPath("$.result.content.[0].minChangeValue").value(0L))
                .andExpect(jsonPath("$.result.content.[0].minValue").value(0L))
                .andExpect(jsonPath("$.result.content.[0].maxValue").value(100L))
                .andExpect(jsonPath("$.result.content.[0].defaultValue").value(0L))
                .andExpect(jsonPath("$.result.content.[0].status").value("READY"))
                .andExpect(jsonPath("$.result.content.[0].uptId").value("test@test.com"))
                .andExpect(jsonPath("$.result.content.[0].regId").value("test@test.com"))
                .andExpect(jsonPath("$.result.content.[0].createdDate").exists())
                .andExpect(jsonPath("$.result.content.[0].updatedDate").exists())
                .andExpect(jsonPath("$.result.total_pages").value("1"))
                .andExpect(jsonPath("$.result.total_elements").value("3"))
                .andExpect(jsonPath("$.result.page").value("0"))
                .andExpect(jsonPath("$.result.size").value("10"))
        ;
    }

    @Test
    public void testDuplication() throws Exception {

        //given
        val givenServiceId = "testServiceId";
        val givenStatsName= "stats";

        when(service.duplicate(givenStatsName, givenServiceId)).thenReturn(true);

        //when
        mockMvc
                .perform(
                        RestDocumentationRequestBuilders.get("/internal/metadata/duplication")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .param("serviceId","testServiceId")
                                .param("statsName", "stats")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        requestParameters(
                                parameterWithName("serviceId").description("service Id"),
                                parameterWithName("statsName").description("스텟 이름").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("result").description("결과")
                        )
                ))
                //then
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result").value("true"))
        ;
    }


}
