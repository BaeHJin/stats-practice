package front.service;

import core.data.Stats;
import core.data.UserStats;
import core.data.type.*;
import front.data.User;
import front.data.type.UserType;
import front.exception.NotFoundUserException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Optional;

import static org.mockito.Mockito.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {KafkaConfig.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStatsServiceTest {

    UserService userService;
    core.service.userstats.UserStatsService userStatsService;
    UserStatsService service;
    ApplicationEventPublisher eventPublisher;
    Stats givenStats;

    @Before
    public  void setUp(){

        userService = mock(UserService.class);
        userStatsService = mock( core.service.userstats.UserStatsService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service =  new UserStatsService(userService, userStatsService, eventPublisher);

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
    public void testGetOne() {

        //given
        val givenUserId = "00700";
        val givenStatsId = "testStats";
        val givenGameId = "testGame";
        val givenType = UserType.S;
        val givenUser =  new User(new User.UserId(UserType.S, "100", "testGame", null, givenUserId));
        val expected = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());

        when(userService.getOne(givenGameId, givenUserId, givenType)).thenReturn(Optional.ofNullable(givenUser));
        when(userStatsService.get(givenUser.getUserId(), givenStatsId, givenGameId)).thenReturn(expected);

        //when
        val actual = service.get(givenUserId, givenStatsId, givenGameId, givenType);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void testGetOneNotExist() {

        //given
        val givenUserId = "00700";
        val givenStatsId = "testStats";
        val givenGameId = "testGame";
        val givenType = UserType.S;
        val user = new User(new User.UserId(UserType.S, "101", "testGame", null, "123"));

        val expected = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 0L, System.currentTimeMillis());

        when(userService.getOne(givenGameId, givenUserId, givenType)).thenReturn(Optional.ofNullable(null));
        when(userService.setUser(givenGameId, givenUserId, givenType)).thenReturn(user);
        when(userStatsService.get(user.getUserId(), givenStatsId, givenGameId)).thenReturn(expected);

        //when
        val actual = service.get(givenUserId, givenStatsId, givenGameId, givenType);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void testGetList() {
        //given
        val currentTime = DateTime.now();
        val givenGameId = "testGame";
        val givenStatsId = "testStats";
        val givenStatsId2 = "testStats2";

        Pageable pageable = PageRequest.of(0, 10);
        Long count = 10L;

        val user = new User(new User.UserId(UserType.S, "101", "testGame", null, "123"));
        val user2 = new User(new User.UserId(UserType.S, "102", "testGame", null, "456"));
        val users = Lists.newArrayList(user, user2);
        val requestUsers = Lists.newArrayList(user.getId(), user2.getId());
        val usersIds = Lists.newArrayList(user.getUserId(), user2.getUserId());

        val givenUserStats = Lists.newArrayList(
                UserStats.builder()
                        .id(new UserStats.UserStatsId(user.getUserId(), givenStatsId, givenGameId))
                        .value(7L)
                        .updatedDate(currentTime)
                .build(),
                UserStats.builder()
                        .id(new UserStats.UserStatsId(user.getUserId(), givenStatsId2, givenGameId))
                        .value(9L)
                        .updatedDate(currentTime)
                        .build(),
                UserStats.builder()
                        .id(new UserStats.UserStatsId(user2.getUserId(), givenStatsId, givenGameId))
                        .value(5L)
                        .updatedDate(currentTime)
                        .build()
        );

        Page<UserStats> pageUserStats = PageableExecutionUtils.getPage(
                givenUserStats,
                pageable,
                () -> count);

        val givenUserStats1 = Lists.newArrayList(
                                core.service.userstats.UserStatsService.SearchUserStats.UserStats.builder()
                                        .statsId(givenStatsId)
                                        .value(7L)
                                        .updatedDate(currentTime.toInstant().getMillis())
                                        .build(),
                                core.service.userstats.UserStatsService.SearchUserStats.UserStats.builder()
                                        .statsId(givenStatsId2)
                                        .value(9L)
                                        .updatedDate(currentTime.toInstant().getMillis())
                                        .build()
                        );
        val givenUserStats2 = Lists.newArrayList(
                                core.service.userstats.UserStatsService.SearchUserStats.UserStats.builder()
                                        .statsId(givenStatsId)
                                        .value(5L)
                                        .updatedDate(currentTime.toInstant().getMillis())
                                        .build()
                        );


        when(userService.get(requestUsers)).thenReturn(users);
        when(userService.getUserIdsFromUsers(users)).thenReturn(usersIds);
        when(userStatsService.get(givenGameId, usersIds, pageable)).thenReturn(pageUserStats);


        val expected = Lists.newArrayList(
                UserStatsService.SearchUserStatsResponse.of(user, givenUserStats1),
                UserStatsService.SearchUserStatsResponse.of(user2, givenUserStats2)
        );

        //when
        val actual = service.get(requestUsers, pageable);

        //then
        Assertions.assertThat(actual.getContent()).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(expected);
    }

    @Test
    public void testGetListResultIsNullBecauseUserIsEmpty() {
        //given

        Pageable pageable = PageRequest.of(0, 10);

        val user = new User(new User.UserId(UserType.S, "101", "testGame", null, "123"));
        val user2 = new User(new User.UserId(UserType.S, "102", "testGame", null, "456"));
        val requestUsers = Lists.newArrayList(user.getId(), user2.getId());

        when(userService.get(requestUsers)).thenReturn(null);

        //when
        val actual = service.get(requestUsers, pageable);

        //then
        Assertions.assertThat(actual).isNull();
    }


    //todo verify uuid ignore 하는 법 찾기
    @Test
    public void testPutByUserTypeExistUser() throws InterruptedException {

        //given
        val givenUserId = "00700";
        val givenStatsId = "testStats";
        val givenGameId = "testGame";
        val givenType = UserType.S;
        val givenValue = 10L;
        val givenUser =  new User(new User.UserId(UserType.S, "100", givenGameId, null, givenUserId));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val givenResponse = core.service.userstats.UserStatsService.PutResponse.builder()
                .prevValue(1L)
                .userStats(givenUserStats)
                .updateType(givenStats.getUpdateType())
                .build();

        when(userService.getOne(givenGameId, givenUserId, givenType)).thenReturn(Optional.ofNullable(givenUser));
        when(userStatsService.put(givenUser.getUserId(), givenStatsId, givenGameId, givenValue, PermissionType.ALL)).thenReturn(givenResponse);


        //when
        val actual = service.put(givenUserId, givenStatsId, givenGameId, givenValue, givenType, PermissionType.ALL);

        //then
        Assertions.assertThat(actual).isEqualTo(givenUserStats);

    }

    //todo verify uuid ignore 하는 법 찾기
    @Test
    public void testPutByUserTypeNotExistUser() throws InterruptedException {

        //given
        val givenUserId = "00700";
        val givenStatsId = "testStats";
        val givenGameId = "testGame";
        val givenType = UserType.S;
        val givenValue = 10L;
        val givenUser =  new User(new User.UserId(UserType.S, "100", givenGameId, null, givenUserId));
        val expected = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());

        val givenResponse = core.service.userstats.UserStatsService.PutResponse.builder()
                .prevValue(1L)
                .userStats(expected)
                .updateType(givenStats.getUpdateType())
                .build();


        when(userService.getOne(givenGameId, givenUserId, givenType)).thenReturn(Optional.ofNullable(null));
        when(userService.setUser(givenGameId, givenUserId, givenType)).thenReturn(givenUser);
        when(userStatsService.put(givenUser.getUserId(), givenStatsId, givenGameId, givenValue, PermissionType.ALL)).thenReturn(givenResponse);

        //when
        val actual = service.put(givenUserId, givenStatsId, givenGameId, givenValue, givenType, PermissionType.ALL);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);

    }

    @Test(expected = NotFoundUserException.class)
    public void testPutByUserTypeNotFoundUserException() throws InterruptedException {

        //given
        val givenUserId = "00700";
        val givenStatsId = "testStats";
        val givenGameId = "testGame";
        val givenType = UserType.S;
        val givenValue = 10L;
        val givenUser =  new User(new User.UserId(UserType.S, "100", givenGameId, null, givenUserId));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());

        val givenResponse = core.service.userstats.UserStatsService.PutResponse.builder()
                .prevValue(1L)
                .userStats(givenUserStats)
                .updateType(givenStats.getUpdateType())
                .build();

        when(userService.getOne(givenGameId, givenUserId, givenType)).thenReturn(Optional.ofNullable(null));
        when(userService.setUser(givenGameId, givenUserId, givenType)).thenThrow(new NotFoundUserException("Invalid character"));
        when(userStatsService.put(givenUser.getUserId(), givenStatsId, givenGameId, givenValue, PermissionType.ALL)).thenReturn(givenResponse);

        //when
        service.put(givenUserId, givenStatsId, givenGameId, givenValue, givenType, PermissionType.ALL);

    }


}
