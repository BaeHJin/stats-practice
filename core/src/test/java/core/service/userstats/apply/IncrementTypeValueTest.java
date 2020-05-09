package core.service.userstats.apply;


import core.data.Stats;
import core.data.UserStats;
import core.data.type.*;
import core.exception.UserStatsUpdateException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncrementTypeValueTest {

    IncrementTypeValue service;

    Stats givenStats;
    UserStats givenPrevUserStats;

    @Before
    public void before() {

        service = new IncrementTypeValue();

        givenStats = Stats.builder().id(new Stats.Id("testStats_1_1", "testService_1"))
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(10L).minChangeValue(0L)
                .minValue(0L).maxValue(20L).defaultValue(0L)
                .regId("hjbae@sg.com").uptId("hjbae@sg.com")
                .createdDate(new DateTime()).updatedDate(new DateTime())
                .build();

    }

    @Test
    public void testApplyValue(){

        //given
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(1L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(5L);
    }

    @Test
    public void testApplyValueNoPrevUserStats(){

        //given
        val givenLongValue = 5L;

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(null));

        //then
        assertThat(actual).isEqualTo(5L);

    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueAlreadyFullMaxValue(){    
        
        
        //given
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(20L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then

    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueAlreadyFullMinValue(){

        //given
        val givenLongValue = -5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(0L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then

    }

    @Test
    public void testApplyValueOverMaxValue(){

        //given
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(17L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(3L);

    }

    @Test
    public void testApplyValueOverMinValue(){

        //given
        val givenLongValue = -5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(3L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(-3L);

    }

    /* Value Type is DOUBLE test */

    @Test
    public void testApplyValueDoubleType(){

        //given
        val givenLongValue = 5L;
        givenStats.setValueType(ValueType.DOUBLE);
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(1000L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(5000L);

    }

    @Test
    public void testApplyValueNoPrevUserStatsDoubleType(){

        //given
        val givenLongValue = 5L;
        givenStats.setValueType(ValueType.DOUBLE);

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(null));

        //then
        assertThat(actual).isEqualTo(5000L);

    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueAlreadyFullMaxValueDoubleType(){

        //given
        val givenLongValue = 5L;
        givenStats.setValueType(ValueType.DOUBLE);
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(20000L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then

    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueAlreadyFullMinValueDoubleType(){

        //given
        val givenLongValue = -5L;
        givenStats.setValueType(ValueType.DOUBLE);
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(0L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then

    }

    @Test
    public void testApplyValueOverMaxValueDoubleType(){

        //given
        val givenLongValue = 5L;
        givenStats.setValueType(ValueType.DOUBLE);
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(17000L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(3000L);

    }

    @Test
    public void testApplyValueOverMinValueDoubleType(){

        //given
        val givenLongValue = -5L;
        givenStats.setValueType(ValueType.DOUBLE);
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(new ObjectId(), "testStats_1_1", "testService_1"))
                .value(3000L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(-3000L);

    }

}
