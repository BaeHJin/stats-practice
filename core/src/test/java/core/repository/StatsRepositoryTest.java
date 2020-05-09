package core.repository;

import com.google.common.collect.Sets;
import core.config.MongoConfig;
import core.data.Stats;
import core.data.type.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoConfig.class, StatsRepository.class})
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsRepositoryTest {

    @Autowired StatsRepository repository;

    @Autowired MongoTemplate mongoTemplate;

    String description = "des";
    ValueType valueType = ValueType.INT;
    PermissionType permissionType = PermissionType.ALL;
    UpdateType updateType = UpdateType.INCREMENT;
    Long maxChangeValue = 0L;
    Long minChangeValue = 0L;
    Long minValue = 0L;
    Long maxValue = 0L;
    Long defaultValue = 0L;
    StatusType status = StatusType.OK;
    String regId = "hjbae@sg.com";
    String uptId = "hjbae@sg.com";
    DateTime createdDate = new DateTime(1570508918000L);
    DateTime updatedDate = new DateTime(1570508918000L);
    List<Stats> savedStats;

    @Before
    public void setUp() {

        mongoTemplate.dropCollection("stats");

        savedStats = Lists.newArrayList(
                Stats.builder().id(new Stats.Id("testStats_1_1", "testService_1"))
                        .description("test")
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build(),
                Stats.builder().id(new Stats.Id("testStats_1_2", "testService_1"))
                        .description(description)
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build(),
                Stats.builder().id(new Stats.Id("testStats_1_3", "testService_1"))
                        .description(description)
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build(),
                Stats.builder().id(new Stats.Id("testStats_2_1", "testService_2"))
                        .description(description)
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build(),
                Stats.builder().id(new Stats.Id("testStats_2_2", "testService_2"))
                        .description(description)
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build(),
                Stats.builder().id(new Stats.Id("deleteTest", "testService_1"))
                        .description(description)
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(StatusType.INACTIVE)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build()

        );

        mongoTemplate.insertAll(Lists.newArrayList(savedStats));
    }

    @Test
    public void testSave() {

        // given
        val stats = Stats.builder().id(new Stats.Id("saveTestStats", "saveTestService"))
                        .description(description)
                        .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                        .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                        .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                        .regId(regId).uptId(uptId)
                        .createdDate(createdDate).updatedDate(updatedDate)
                        .build();

        // when
        val actual = repository.update(stats);

        // then
        Assert.assertEquals(actual.getId().getStatsId(), "saveTestStats");
        Assert.assertEquals(actual.getId().getServiceId(), "saveTestService");

    }

    @Test
    public void testUpdate() {

        // given
        val stats = Stats.builder().id(new Stats.Id("saveTestStats", "saveTestService"))
                .description("UPDATE_TEST")
                .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                .regId(regId).uptId(uptId)
                .createdDate(createdDate).updatedDate(updatedDate)
                .build();
        // when
        val actual = repository.update(stats);

        // then
        Assert.assertEquals(actual.getId().getServiceId(), "saveTestService");
        Assert.assertEquals(actual.getId().getStatsId(), "saveTestStats");
        Assert.assertEquals(actual.getDescription(), "UPDATE_TEST");

    }


    @Test
    public void testFindOne() {

        // given
//        val givenQuery = findOne(new Stats.StatsId("testStats_1_1", "testService_1"));

        val expectedResult = Stats.builder().id(new Stats.Id("testStats_1_1", "testService_1"))
                .description("test")
                .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                .regId(regId).uptId(uptId)
                .createdDate(createdDate).updatedDate(updatedDate)
                .build();

        // when
        val actual = repository.findOne("testStats_1_1", "testService_1");


        // then
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(expectedResult, actual.get());

    }


    @Test
    public void testNotFoundOne() {

        // given
//        val givenQuery = findOne(new Stats.StatsId("testStats_0_0", "testService_1"));
//        when()
        // when
        val actual = repository.findOne("testStats_0_0", "testService_1");

        // then
        Assert.assertTrue(!actual.isPresent());

    }



    @Test
    public void testInsert() {

        // given
        val stats = Stats.builder().id(new Stats.Id("saveTestStats", "saveTestService"))
                .description("UPDATE_TEST")
                .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                .regId(regId).uptId(uptId)
                .createdDate(createdDate).updatedDate(updatedDate)
                .build();
        // when
        val actual = repository.insert(stats);

        // then
        Assert.assertEquals(actual.getId().getServiceId(), "saveTestService");
        Assert.assertEquals(actual.getId().getStatsId(), "saveTestStats");
        Assert.assertEquals(actual.getDescription(), "UPDATE_TEST");


    }

    @Test(expected = DuplicateKeyException.class)
    public void testInsertFail() {

        // given
        val stats = Stats.builder().id(new Stats.Id("testStats_1_1", "testService_1"))
                .description("UPDATE_TEST")
                .valueType(valueType).permissionType(permissionType).updateType(updateType).status(status)
                .maxChangeValue(maxChangeValue).minChangeValue(minChangeValue)
                .minValue(minValue).maxValue(maxValue).defaultValue(defaultValue)
                .regId(regId).uptId(uptId)
                .createdDate(createdDate).updatedDate(updatedDate)
                .build();

        // when
        repository.insert(stats);

        // then
    }

    @Test
    public void testFind() {

        // given
        val givenServiceId = "testService_1";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        val actual = repository.find(givenServiceId, null, StatusType.OK, null, pageable);

        // then
        Assertions.assertThat(actual.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void testFindWithStatsId() {

        // given
        val givenServiceId = "testService_1";
        val givenStatsId = "testStats_1_1";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        val actual = repository.find(givenServiceId, givenStatsId, StatusType.OK, null, pageable);

        // then
        Assertions.assertThat(actual.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testFindWithTitle() {

        // given
        val givenServiceId = "testService_1";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        val actual = repository.find(givenServiceId, null, StatusType.OK, "tes", pageable);

        // then
        Assertions.assertThat(actual.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testDelete() {

        // given
        val deleteStatsId = Sets.newHashSet(new Stats.Id("deleteTest", "testService_1"));
        val expected = Sets.newHashSet("deleteTest");

        // when
        val actual = repository.delete(deleteStatsId);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testfind() {

        // given

        // when
        val actual = repository.find("testService_1");

        // then
        Assertions.assertThat(actual).size().isEqualTo(4);
    }

    @Test
    public void testFindQuery(){

        // given
        val serviceId = "testService";
        val statsId = "testStatsId";
        val status = StatusType.OK;
        val title = "testTitle";
        val pageable = PageRequest.of(0, 10);
        Query expected = new Query();
        expected.addCriteria(Criteria.where("_id").is(new Stats.Id(statsId, serviceId)))
                .addCriteria(Criteria.where("status").is(status))
                .addCriteria(Criteria.where("description").regex(".*"+title+".*"))
                .with(pageable);

        // when
        val actual = StatsRepository.findQuery(serviceId, statsId, status, title, pageable);

        // then
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void testFindQuery_case_empty_statsId(){

        // given
        val serviceId = "testService";
        String statsId = null;
        val status = StatusType.OK;
        val title = "testTitle";
        val pageable = PageRequest.of(0, 10);
        Query expected = new Query();
        expected.addCriteria(Criteria.where("_id.serviceId").is(serviceId))
                .addCriteria(Criteria.where("status").is(status))
                .addCriteria(Criteria.where("description").regex(".*"+title+".*"))
                .with(pageable);

        // when
        val actual = StatsRepository.findQuery(serviceId, statsId, status, title, pageable);

        // then
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void testFindQuery_case_empty_statsId_and_status(){

        // given
        val serviceId = "testService";
        String statsId = null;
        StatusType status = null;
        val title = "testTitle";
        val pageable = PageRequest.of(0, 10);
        Query expected = new Query();
        expected.addCriteria(Criteria.where("_id.serviceId").is(serviceId))
                .addCriteria(Criteria.where("description").regex(".*"+title+".*"))
                .with(pageable);

        // when
        val actual = StatsRepository.findQuery(serviceId, statsId, status, title, pageable);

        // then
        assertThat(actual).isEqualTo(expected);

    }

}
