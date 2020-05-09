package core.service.stats;

//import core.config.CacheConfig;
import core.config.CacheConfig;
import core.data.Stats;
import core.data.type.PermissionType;
import core.data.type.StatusType;
import core.data.type.UpdateType;
import core.data.type.ValueType;
import core.exception.NotFoundDataException;
import core.repository.StatsRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { CacheConfig.class })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Cacheable
public class StatsCacheServiceTest {

    StatsCacheService service;

    StatsRepository repository;

    Stats givenStats;

    @Before
    public void setUp() {

        repository = mock(StatsRepository.class);
        service = new StatsCacheService(repository);


        givenStats =  Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();



    }

    @Test
    public void testGet() {

        // given
        val givenStatsId = "testStats";
        val givenServiceId = "testService";
        when(repository.findOne("testStats", "testService")).thenReturn(Optional.ofNullable(givenStats));

        //when
        val actual = service.get(givenStatsId, givenServiceId);

        //them
        Assertions.assertThat(actual).isEqualTo(givenStats);

    }


    @Test
    public void testGetNoCache() {

        // given
        val givenStatsId = "testStats2";
        val givenServiceId = "testService2";
        val givenStats2 =  Stats.builder().id(new Stats.Id("testStats2", "testService2"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(new DateTime(1570508918000L))
                .build();

        when(repository.findOne("testStats2", "testService2")).thenReturn(Optional.ofNullable(givenStats2));

        // when
        val actual = service.get(givenStatsId, givenServiceId);

        // then
        Assertions.assertThat(actual).isEqualTo(givenStats2);
    }


    @Test(expected = NotFoundDataException.class)
    public void testGetNoCacheNotFoundDataException() {

        // given
        val givenStatsId = "testStats2";
        val givenServiceId = "testService2";

        when(repository.findOne("testStats2", "testService2")).thenReturn(Optional.ofNullable(null));

        // when
        service.get(givenStatsId, givenServiceId);

    }


    @Test
    public void testGetByServiceId() {

        // given
        val givenServiceId = "testService";
        when(repository.find(givenServiceId)).thenReturn(Lists.newArrayList(givenStats));

        //when
        val actual = service.get(givenServiceId);

        //them
        Assertions.assertThat(actual).contains(givenStats);

    }

    @Test(expected = NotFoundDataException.class)
    public void testGetByServiceIdIsEmpty() {

        // given
        val givenServiceId = "testService";
        when(repository.find(givenServiceId)).thenReturn(Lists.newArrayList());

        //when
        service.get(givenServiceId);
    }

}
