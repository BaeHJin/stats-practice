package core.repository;

import com.google.common.collect.Lists;
import core.config.MongoConfig;
import core.data.Stats;
import core.data.UserStats;
import core.data.type.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoConfig.class, UserStatsRepository.class, ServiceRepository.class})
//@EnableAutoConfiguration
// 실제 원격 테스트 하려면,
// -- 아래 주석을 해제하고
// -- com.github.tomakehurst.wiremock.client.WireMock.verify(RequestPatternBuilder) 부분을 주석처리 한다.
//@TestPropertySource(
//        properties = {
//                "client.mobile.host=localhost"
//        }
//)
@AutoConfigureWireMock(port = 0)
@Slf4j
public class UserStatsRepositoryTest {

    private UserStatsRepository repository;
    private ServiceRepository serviceRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    Stats givenStats;
    Stats givenStats2;
    Stats givenStats3;

    ObjectId testUser  = new ObjectId();
    ObjectId testUser2  = new ObjectId();

    ObjectId givenUserId = testUser;
    ObjectId givenUserId2 = testUser2;

    @Before
    public void setUp() {

        serviceRepository = mock(ServiceRepository.class);
        repository = new UserStatsRepository(mongoTemplate, serviceRepository);

        mongoTemplate.dropCollection("userStats");
        mongoTemplate.dropCollection("userStats_other_service");


        val givenStatsId = "testStats";
        val givenStatsId2 = "testStats2";
        val givenStatsId3 = "testStats3";
        val givenServiceId = "testService";
        val givenServiceId2 = "testService_test";

        val givenValue = 1L;
        val givenValue2 = 2L;

        givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(10L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@s.com").uptId("test@s.com")
                .createdDate(new DateTime()).updatedDate(new DateTime())
                .build();

        when(serviceRepository.getCollectionNameSuffix(givenServiceId)).thenReturn(Optional.empty());

        repository.update(givenUserId, givenStatsId, givenServiceId, givenValue, givenStats.getUpdateType());
        repository.update(givenUserId, givenStatsId2, givenServiceId, givenValue2, givenStats.getUpdateType());

        repository.update(givenUserId2, givenStatsId, givenServiceId, givenValue, givenStats.getUpdateType());
        repository.update(givenUserId2, givenStatsId2, givenServiceId, givenValue2, givenStats.getUpdateType());

        givenStats2 = Stats.builder().id(new Stats.Id("testStats", "testService_test"))
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(10L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@s.com").uptId("test@s.com")
                .createdDate(new DateTime()).updatedDate(new DateTime())
                .build();

        when(serviceRepository.getCollectionNameSuffix(givenServiceId2)).thenReturn(Optional.of("userStats_other_service"));

        repository.update(givenUserId, givenStatsId, givenServiceId2, givenValue, givenStats2.getUpdateType());
        repository.update(givenUserId, givenStatsId2, givenServiceId2, givenValue2, givenStats2.getUpdateType());

        repository.update(givenUserId2, givenStatsId, givenServiceId2, givenValue, givenStats2.getUpdateType());
        repository.update(givenUserId2, givenStatsId2, givenServiceId2, givenValue2, givenStats2.getUpdateType());

        givenStats3 = Stats.builder().id(new Stats.Id("testStats3", "testService_test"))
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.REPLACE).status(StatusType.OK)
                .maxChangeValue(10L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@s.com").uptId("test@s.com")
                .createdDate(new DateTime()).updatedDate(new DateTime())
                .build();

        when(serviceRepository.getCollectionNameSuffix(givenServiceId2)).thenReturn(Optional.of("userStats_other_service"));

        repository.update(givenUserId, givenStatsId3, givenServiceId2, givenValue, givenStats3.getUpdateType());
        repository.update(givenUserId2, givenStatsId3, givenServiceId2, givenValue, givenStats3.getUpdateType());

    }


    @Test
    public void testFindOne() {

        //given
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val findUserStats = new UserStats.UserStatsId(givenUserId, "testStats", "testService");

        //when
        val actual = repository.findOne(givenUserId, givenStatsId, givenServiceId);

        //then
        Assert.assertEquals((long) actual.get().getValue(), 1L);
        Assert.assertEquals(actual.get().getId(), findUserStats);

    }

    @Test
    public void testFindOne_CASE_REPLACE() {

        //given
        val givenStatsId = "testStats3";
        val givenServiceId = "testService_test";
        val findUserStats = new UserStats.UserStatsId(givenUserId, "testStats3", "testService_test");

        //when
        val actual = repository.findOne(givenUserId, givenStatsId, givenServiceId);

        //then
        Assert.assertEquals((long) actual.get().getValue(), 1L);
        Assert.assertEquals(actual.get().getId(), findUserStats);

    }

    @Test
    public void testFindOneDynamicCollection() {

        //given
        val givenStatsId = "testStats";
        val givenServiceId2 = "testService_test";
        val findUserStats = new UserStats.UserStatsId(givenUserId, "testStats", "testService_test");

        //when
        val actual = repository.findOne(givenUserId, givenStatsId, givenServiceId2);

        //then
        Assert.assertEquals((long) actual.get().getValue(), 1L);
        Assert.assertEquals(actual.get().getId(), findUserStats);

    }


    @Test
    public void testFindOneNotFoundResultIsNull() {

        //given
        val givenStatsId = "testStats1111";
        val givenServiceId = "testService";

        //when
        val actual = repository.findOne(givenUserId, givenStatsId, givenServiceId);

        //then
        Assert.assertTrue(!actual.isPresent());

    }


    @Test
    public void testUpdate() {

        //given
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenValue = 1L;

        //when
        val actual = repository.update(testUser, givenStatsId, givenServiceId, givenValue, givenStats.getUpdateType());

        //then
        assertThat(actual.getId().getUserId()).isEqualTo(testUser);
        assertThat(actual.getId().getStatsId()).isEqualTo(givenStatsId);
        assertThat(actual.getId().getServiceId()).isEqualTo(givenServiceId);
        assertThat(actual.getValue()).isEqualTo(2L);

    }

    @Test
    public void testUpdate_CASE_REPLACE() {

        //given
        val givenStatsId = "testStats3";
        val givenServiceId = "testService_test";
        val givenValue = 9L;

        //when
        val actual = repository.update(testUser, givenStatsId, givenServiceId, givenValue, givenStats3.getUpdateType());

        //then
        assertThat(actual.getId().getUserId()).isEqualTo(testUser);
        assertThat(actual.getId().getStatsId()).isEqualTo(givenStatsId);
        assertThat(actual.getId().getServiceId()).isEqualTo(givenServiceId);
        assertThat(actual.getValue()).isEqualTo(9L);

    }

    @Test
    public void testUpdateDynamicCollection() {

        //given
        val givenStatsId = "testStats";
        val givenServiceId = "testService_test";
        val givenValue = 1L;

        //when
        val actual = repository.update(testUser, givenStatsId, givenServiceId, givenValue, givenStats.getUpdateType());

        //then
        assertThat(actual.getId().getUserId()).isEqualTo(testUser);
        assertThat(actual.getId().getStatsId()).isEqualTo(givenStatsId);
        assertThat(actual.getId().getServiceId()).isEqualTo(givenServiceId);
        assertThat(actual.getValue()).isEqualTo(2L);

    }

    @Test
    public void testFind() {

        //given
       val givenUserIds = Lists.newArrayList(testUser, testUser2);
       val givenServiceId = "testService";
       val givenStatsIds = Lists.newArrayList("testStats");
       val pageable = PageRequest.of(0, 10);

        //when
        val actual = repository.find(givenUserIds, givenServiceId, givenStatsIds, pageable);

        //then
        assertThat(actual.getTotalElements()).isEqualTo(2);

        for(int i = 0; i<actual.getTotalElements(); i++){
            assertThat(actual.getContent().get(i).getId().getStatsId()).isEqualTo("testStats");
            assertThat(actual.getContent().get(i).getId().getServiceId()).isEqualTo("testService");
        }

    }

    @Test
    public void testFindDynamicCollection() {

        //given
        val givenUserIds = Lists.newArrayList(testUser, testUser2);
        val givenServiceId = "testService_test";
        val givenStatsIds = Lists.newArrayList("testStats2");
        val pageable = PageRequest.of(0, 10);

        //when
        val actual = repository.find(givenUserIds, givenServiceId, givenStatsIds, pageable);

        //then
        assertThat(actual.getTotalElements()).isEqualTo(2);

        for(int i = 0; i<actual.getTotalElements(); i++){
            assertThat(actual.getContent().get(i).getId().getStatsId()).isEqualTo("testStats2");
            assertThat(actual.getContent().get(i).getId().getServiceId()).isEqualTo("testService_test");
        }

    }





}
