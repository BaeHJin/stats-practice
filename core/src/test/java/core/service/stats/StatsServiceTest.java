package core.service.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.data.Stats;
import core.data.type.*;
import core.exception.DuplicationException;
import core.exception.NotFoundDataException;
import core.repository.StatsRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsServiceTest {

    StatsService service;
    StatsRepository repository;

    ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setUp() {

        repository = mock(StatsRepository.class);
        service = new StatsService(repository);

    }

    @Test
    public void testGet() {

        // given
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenResponse =  Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();

        // when
        when(repository.findOne(givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(givenResponse));

        val actual = service.get(givenStatsId, givenServiceId);

        // then
        Assert.assertEquals(actual.getId().getStatsId(), "testStats");
        Assert.assertEquals(actual.getId().getServiceId(), "testService");
    }


    @Test(expected = NotFoundDataException.class)
    public void testGetNotFound() {

        // given
        val givenStatsId = "testStats_not_exist";
        val givenServiceId = "testService";

        // when
        when(repository.findOne(givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(null));

        service.get(givenStatsId, givenServiceId);

        // then
    }

    @Test
    public void testInsert() {

        // given
        val givenStats =  Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();

        val expected =  StatsService.StatsResult.builder().statsName("testStats")
                .serviceId("testService")
                .statsId("testService|testStats")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(1570508918000L).updatedDate(1570508918000L)
                .build();
        // when
        when(repository.insert(givenStats)).thenReturn(givenStats);

        val actual = service.insert(givenStats);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = DuplicationException.class)
    public void testInsertDuplicationException() {

        // given
        val givenStats =  Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();
        // when
        when(repository.insert(givenStats)).thenThrow(new DuplicateKeyException("Stats key is Duplication."));

        service.insert(givenStats);
    }

    @Test
    public void testDuplicate() {

        // given
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        val givenStats =  Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();

        // when
        when(repository.findOne(givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(givenStats));

         val actual = service.duplicate(givenStatsId, givenServiceId);
        assertThat(actual).isTrue();
    }

    @Test
    public void testDuplicateIsFalse() {

        // given
        val givenStatsId = "testStats";
        val givenServiceId = "testService";

        // when
        when(repository.findOne(givenStatsId, givenServiceId)).thenReturn(Optional.ofNullable(null));

        val actual = service.duplicate(givenStatsId, givenServiceId);
        assertThat(actual).isFalse() ;
    }

    @Test
    public void testGetByServiceId() {

        // given
        val givenServiceId = "testService";
        val givenResponse = Lists.newArrayList(
                Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build(),
                Stats.builder().id(new Stats.Id("testStats2", "testService"))
                        .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                        .maxChangeValue(100L).minChangeValue(0L)
                        .minValue(0L).maxValue(100L).defaultValue(0L)
                        .regId("test@test.com").uptId("test@test.com")
                        .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                        .build(),
                Stats.builder().id(new Stats.Id("testStats3", "testService"))
                        .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                        .maxChangeValue(100L).minChangeValue(0L)
                        .minValue(0L).maxValue(100L).defaultValue(0L)
                        .regId("test@test.com").uptId("test@test.com")
                        .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                        .build()
        );

        val expected = Lists.newArrayList(
                StatsService.StatsResult.builder()
                        .statsId("testService|testStats")
                        .statsName("testStats")
                        .serviceId("testService")
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
                        .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                        .maxChangeValue(100L).minChangeValue(0L)
                        .minValue(0L).maxValue(100L).defaultValue(0L)
                        .regId("test@test.com").uptId("test@test.com")
                        .createdDate(1570508918000L).updatedDate(1570508918000L)
                        .build()
        );

        Page<Stats> page = new PageImpl<>(givenResponse);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        when(repository.find(givenServiceId, null, null, null, pageable)).thenReturn(page);

        val actual = service.get(givenServiceId, null, null, null, pageable);

        // then
        assertThat(actual.getTotalPages()).isEqualTo(1);
        assertThat(actual.getTotalElements()).isEqualTo(3);
        assertThat(actual.getPage()).isEqualTo(0);
        assertThat(actual.getSize()).isEqualTo(10);
        assertThat(actual.getContent()).contains(expected.get(0), expected.get(1), expected.get(2));

    }

    @Test(expected = NotFoundDataException.class)
    public void testUpdateNotFoundDataException(){

        val givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.INACTIVE)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();

        when(repository.findOne("testStats13", "testService")).thenReturn(Optional.ofNullable(null));

        service.update(givenStats);

    }

    @Test
    public void testUpdateInactiveType(){

        val givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.INACTIVE)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val givenParamStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.INACTIVE)
                .maxChangeValue(10L).minChangeValue(1L)
                .minValue(0L).maxValue(10L).defaultValue(1L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val expected =  StatsService.StatsResult.builder().statsName("testStats")
                .serviceId("testService")
                .statsId("testService|testStats")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.INACTIVE)
                .maxChangeValue(10L).minChangeValue(1L)
                .minValue(0L).maxValue(10L).defaultValue(1L)
                .regId("test@test.com").uptId("modify@test.com")
                .build();

        when(repository.findOne("testStats", "testService")).thenReturn(Optional.ofNullable(givenStats));
        when(repository.update(givenParamStats)).thenReturn(givenParamStats);

        val actual = service.update(givenParamStats);

        assertThat(actual).isEqualToIgnoringGivenFields(expected,"createdDate", "updatedDate");

    }

    @Test
    public void testDelete(){

        //given
        val statsNames = Sets.newTreeSet("testStats1", "testStats2");
        val statsIds = statsNames.stream().map( id -> new Stats.Id(id, "deleteService") ).collect(Collectors.toSet());


        when(repository.delete(statsIds)).thenReturn(statsNames);

        //when
        val actual = service.delete("deleteService", statsNames);

        //then
        assertThat(actual).isEqualTo(statsNames);


    }

}
