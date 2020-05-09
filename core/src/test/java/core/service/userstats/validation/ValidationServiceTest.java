package core.service.userstats.validation;

import core.data.Stats;
import core.data.type.PermissionType;
import core.data.type.StatusType;
import core.data.type.UpdateType;
import core.data.type.ValueType;
import core.exception.UserStatsValidationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationServiceTest {

    ValidationService service;

    Stats givenStats;

    @Before
    public void before() {

        service = new ValidationService();

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
    public void testValidation(){

        //given
        givenStats.setStatus(StatusType.OK);
        val givenLongValue = 5L;

        //when
        val actual = service.validation(givenStats, givenLongValue , PermissionType.ALL);

        //then
        assertThat(actual).isTrue();
    }


    @Test(expected = UserStatsValidationException.class)
    public void testValidationStatusTypeIsReady(){

        //given
        givenStats.setStatus(StatusType.READY);
        val givenLongValue = 5L;

        //when
        service.validation(givenStats, givenLongValue , PermissionType.ALL);

        //then
    }

    @Test(expected = UserStatsValidationException.class)
    public void testValidationValueGTMaxChangeValue(){

        //given
        givenStats.setStatus(StatusType.OK);
        val givenLongValue = 30L;

        //when
        service.validation(givenStats, givenLongValue , PermissionType.ALL);

        //then

    }

    @Test(expected = UserStatsValidationException.class)
    public void testValidationCheckMinChangeValue(){

        //given
        givenStats.setStatus(StatusType.OK);
        val givenLongValue = -2L;

        //when
        service.validation(givenStats, givenLongValue, PermissionType.ALL);

        //then

    }

    @Test
    public void testValidationCheckPermissionType(){

        //given
        givenStats.setStatus(StatusType.OK);
        givenStats.setPermissionType(PermissionType.ALL);
        val givenLongValue = 5L;

        //when
        val actual = service.validation(givenStats, givenLongValue , PermissionType.SERVER);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    public void testValidationCheckPermissionType_case_server(){

        //given
        givenStats.setStatus(StatusType.OK);
        givenStats.setPermissionType(PermissionType.SERVER);
        val givenLongValue = 5L;

        //when
        val actual = service.validation(givenStats, givenLongValue , PermissionType.SERVER);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    public void testValidationCheckPermissionType_case_client(){

        //given
        givenStats.setStatus(StatusType.OK);
        givenStats.setPermissionType(PermissionType.CLIENT);
        val givenLongValue = 5L;

        //when
        val actual = service.validation(givenStats, givenLongValue , PermissionType.CLIENT);

        //then
        assertThat(actual).isTrue();
    }

    @Test(expected = UserStatsValidationException.class)
    public void testValidationCheckPermissionTypeException(){

        //given
        givenStats.setStatus(StatusType.OK);
        givenStats.setPermissionType(PermissionType.CLIENT);
        val givenLongValue = 5L;

        //when
        service.validation(givenStats, givenLongValue , PermissionType.SERVER);

    }

}
