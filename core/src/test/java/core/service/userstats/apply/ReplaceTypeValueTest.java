package core.service.userstats.apply;

import core.data.Stats;
import core.data.UserStats;
import core.data.type.*;
import core.exception.UserStatsUpdateException;
import lombok.val;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ReplaceTypeValueTest {

    ReplaceTypeValue service;

    Stats givenStats;
    UserStats givenPrevUserStats;
    ObjectId testUser = new ObjectId();
    
    @Before
    public void before() {

        service = new ReplaceTypeValue();

        givenStats = Stats.builder().id(new Stats.Id("testStats_1_1", "testService_1"))
                .description("des")
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.REPLACE).status(StatusType.OK)
                .maxChangeValue(20L).minChangeValue(0L)
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
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(10L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(5L);
    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueEqualPrevValue(){

        //given
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(5L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueLessThenMinValue(){

        //given
        val givenLongValue = -5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(5L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
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
    public void testApplyValueGreaterThanMaxValueNoPrevUserStats(){

        //given
        val givenLongValue = 25L;
        givenPrevUserStats = null;

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueLessThenMinValueNoPrevUserStats(){

        //given
        val givenLongValue = -5L;
        givenPrevUserStats = null;

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////  double  test     //////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testApplyValueDouble(){

        //given
        givenStats.setValueType(ValueType.DOUBLE);
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(10000L)
                .updatedDate(new DateTime())
                .build();

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
        assertThat(actual).isEqualTo(5000L);
    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueAlreadyFullMinValueDouble(){

        //given
        givenStats.setMinValue(10L);
        givenStats.setValueType(ValueType.DOUBLE);
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(10000L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueGreaterThanMaxValueDouble(){

        //given
        givenStats.setValueType(ValueType.DOUBLE);
        val givenLongValue = 25L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(17000L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }

    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueLessThenMinValueDouble(){

        //given
        givenStats.setMinValue(10L);
        givenStats.setValueType(ValueType.DOUBLE);
        val givenLongValue = 5L;
        givenPrevUserStats = UserStats.builder()
                .id(new UserStats.UserStatsId(testUser, "testStats_1_1", "testService_1"))
                .value(17000L)
                .updatedDate(new DateTime())
                .build();

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }

    @Test
    public void testApplyValueNoPrevUserStatsDouble(){

        //given
        givenStats.setValueType(ValueType.DOUBLE);
        val givenLongValue = 5L;

        //when
        val actual = service.applyValue(givenStats, givenLongValue, Optional.ofNullable(null));

        //then
        assertThat(actual).isEqualTo(5000L);

    }



    @Test(expected = UserStatsUpdateException.class)
    public void testApplyValueGreaterThanMaxValueNoPrevUserStatsDouble(){

        //given
        givenStats.setValueType(ValueType.DOUBLE);
        val givenLongValue = 25L;
        givenPrevUserStats = null;

        //when
        service.applyValue(givenStats, givenLongValue, Optional.ofNullable(givenPrevUserStats));

        //then
    }


}
