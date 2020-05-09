package core.service.userstats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.data.*;
import core.data.type.*;
import core.exception.NotFoundDataException;
import core.exception.UserStatsUpdateException;
import core.repository.UserStatsRepository;
import core.service.RedisLockService;
import core.service.stats.StatsCacheService;
import core.service.userstats.apply.*;
import core.service.userstats.validation.ValidationService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.util.Lists;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStatsServiceTest {

    UserStatsService service;
    ObjectMapper mapper = new ObjectMapper();

    StatsCacheService statsCacheService;
    ValidationService validationService;
    Map<String, ApplyValue> applyValueServiceMap = new HashMap<>();

    Stats givenStats;

    ApplyValue applyValue;
    UserStatsRepository userStatsRepository;
    RedisLockService redisLockService;

    ObjectId testUser = new ObjectId();

    @Before
    public void setUp(){

        statsCacheService = mock(StatsCacheService.class);
        validationService = mock(ValidationService.class);
        userStatsRepository = mock(UserStatsRepository.class);
        redisLockService = mock(RedisLockService.class);

        applyValueServiceMap.put("replaceTypeValue", mock(ReplaceTypeValue.class));
        applyValueServiceMap.put("incrementTypeValue", mock(IncrementTypeValue.class));
        applyValueServiceMap.put("minTypeValue", mock(MinTypeValue.class));
        applyValueServiceMap.put("maxTypeValue", mock(MaxTypeValue.class));



        service = new UserStatsService(statsCacheService, validationService, applyValueServiceMap, userStatsRepository, redisLockService);

        applyValue = applyValueServiceMap.get("incrementTypeValue");


        givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(20L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@s.com").uptId("test@s.com")
                .createdDate(new DateTime()).updatedDate(new DateTime())
                .build();

    }

    @Test
    public void testGetUserStats() {

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";

        val givenUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats", "testService"))
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();


        val expectedResult = new UserStatsService.UserStatsResponse("testStats", 1L, 1570753488000L);


        //when
        when(statsCacheService.get(givenStatsId, givenServiceId)).thenReturn(givenStats);
        when(userStatsRepository.findOne(givenUserId, givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(givenUserStats));

        val actual = service.get(givenUserId, givenStatsId, givenServiceId);

        //then
        assertThat(actual).isEqualToComparingFieldByField(expectedResult);

    }

    @Test
    public void testGetUserStatsNotFound() {

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";

        //when
        when(statsCacheService.get(givenStatsId, givenServiceId)).thenReturn(givenStats);
        when(userStatsRepository.findOne(givenUserId, givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(null));

        UserStatsService.UserStatsResponse actual = service.get(givenUserId, givenStatsId, givenServiceId);

        //then
        Assert.assertEquals(actual.getStatsId(), givenStatsId);
        Assert.assertEquals(actual.getValue(), givenStats.getDefaultValue());

    }


    @Test(expected = NotFoundDataException.class)
    public void testGetUserStatsNotFoundStats() {

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";

        val givenUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats", "testService"))
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();


        //when
        when(statsCacheService.get(givenStatsId, givenServiceId)).thenThrow(new NotFoundDataException("Not found stats"));
        when(userStatsRepository.findOne(givenUserId, givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(givenUserStats));

        service.get(givenUserId, givenStatsId, givenServiceId);

        //then
    }

    @Test
    public void testPutUserStats() throws InterruptedException {

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenValue = 10L;
        val givenUserStatsId = new UserStats.UserStatsId(givenUserId, givenStatsId, givenServiceId);

        val givenGetUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();

        val givenUpdateUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(11L)
                .build();

        val expectedResult = UserStatsService.PutResponse.of(givenStats, Optional.ofNullable(givenGetUserStats), givenUpdateUserStats);

        //when
        when(applyValue.applyValue(givenStats, givenValue, Optional.ofNullable(givenGetUserStats))).thenReturn(10L);
        when(statsCacheService.get(givenStatsId, givenServiceId)).thenReturn(givenStats);
        when(userStatsRepository.findOne(givenUserId, givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(givenGetUserStats));
        when(userStatsRepository.update(givenUserId, givenStatsId, givenServiceId, givenValue, givenStats.getUpdateType())).thenReturn(givenUpdateUserStats);

        val actual = service.put(givenUserId, givenStatsId, givenServiceId, givenValue, PermissionType.ALL);

        //then
        assertThat(actual.getPrevValue()).isEqualTo(expectedResult.getPrevValue());
        assertThat(actual.getUserStats()).isEqualTo(expectedResult.getUserStats());

    }

    @Test
    public void testPutUserStatsUserStatsUpdateException() throws InterruptedException {

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenValue = 10L;
        val givenUserStatsId = new UserStats.UserStatsId(givenUserId, givenStatsId, givenServiceId);

        val givenGetUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();

        val givenUpdateUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(11L)
                .build();

        val expectedResult = UserStatsService.PutResponse.of(givenStats, Optional.ofNullable(givenGetUserStats));

        //when
        when(applyValue.applyValue(givenStats, givenValue, Optional.ofNullable(givenGetUserStats))).thenThrow(new UserStatsUpdateException("test"));
        when(statsCacheService.get(givenStatsId, givenServiceId)).thenReturn(givenStats);
        when(userStatsRepository.findOne(givenUserId, givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(givenGetUserStats));
        when(userStatsRepository.update(givenUserId, givenStatsId, givenServiceId, givenValue, givenStats.getUpdateType())).thenReturn(givenUpdateUserStats);

        val actual = service.put(givenUserId, givenStatsId, givenServiceId, givenValue, PermissionType.ALL);

        //then
        assertThat(actual.getPrevValue()).isEqualTo(expectedResult.getPrevValue());
        assertThat(actual.getUserStats()).isEqualTo(expectedResult.getUserStats());

    }


    @Test
    public void testPageGet(){

        //given
        val currentTime = DateTime.now();
        val serviceId = "testService";
        val statsId = "testStats";
        val statsIds = Lists.newArrayList(statsId);
        ObjectId userId1 = new ObjectId();
        ObjectId userId2 = new ObjectId();
        val userIds = Lists.newArrayList(userId1, userId2);
        val pageable = PageRequest.of(0, 10);
        Long count = 10L;

        val givenUserStats = Lists.newArrayList(
                UserStats.builder()
                        .id(new UserStats.UserStatsId(userId1, statsId, serviceId))
                        .value(7L)
                        .updatedDate(currentTime)
                        .build(),
                UserStats.builder()
                        .id(new UserStats.UserStatsId(userId2, statsId, serviceId))
                        .value(5L)
                        .updatedDate(currentTime)
                        .build()
        );

        Page<UserStats> pageUserStats = PageableExecutionUtils.getPage(
                givenUserStats,
                pageable,
                () -> count);

        when(statsCacheService.get(serviceId)).thenReturn(Lists.newArrayList(givenStats));
        when(userStatsRepository.find(userIds, serviceId, statsIds, pageable)).thenReturn(pageUserStats);

        val actual = service.get(serviceId, userIds, pageable);

        assertThat(actual).isEqualTo(pageUserStats);
    }

    @Test
    public void testPutResponse_of_case1(){
        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenUserStatsId = new UserStats.UserStatsId(givenUserId, givenStatsId, givenServiceId);

        val givenGetUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();
        // when
        val actual = UserStatsService.PutResponse.of(givenStats, Optional.ofNullable(givenGetUserStats));

        //then
        assertThat(actual.getPrevValue()).isEqualTo(1L);
    }

    @Test
    public void testPutResponse_of_case2(){

        //given

        //when
        val actual = UserStatsService.PutResponse.of(givenStats, Optional.ofNullable(null));

        //then
        assertThat(actual.getPrevValue()).isEqualTo(0L);
    }

    @Test
    public void testPutResponse_of2(){

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenUserStatsId = new UserStats.UserStatsId(givenUserId, givenStatsId, givenServiceId);

        val givenGetUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();

        //when
        val actual = UserStatsService.PutResponse.of(givenStats, Optional.ofNullable(null), givenGetUserStats);

        //then
        assertThat(actual.getPrevValue()).isEqualTo(0L);
    }

    @Test
    public void testPutResponse_of2_case2(){

        //given
        val givenUserId = testUser;
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenUserStatsId = new UserStats.UserStatsId(givenUserId, givenStatsId, givenServiceId);

        val givenGetUserStats = UserStats.builder()
                .id(givenUserStatsId)
                .updatedDate(new DateTime(1570753488000L))
                .value(1L)
                .build();

        //when
        val actual = UserStatsService.PutResponse.of(givenStats, Optional.ofNullable(givenGetUserStats), givenGetUserStats);

        //then
        assertThat(actual.getPrevValue()).isEqualTo(1L);
    }
}
