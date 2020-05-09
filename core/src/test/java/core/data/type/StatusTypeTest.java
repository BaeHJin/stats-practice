package core.data.type;

import core.data.Stats;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Test;

public class StatusTypeTest {


    @Test
    public void testUpdate_INACTIVE() {

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

        val actual = givenParamStats.getStatus().update().apply(givenStats, givenParamStats);

        Assertions.assertThat(actual).isEqualToIgnoringGivenFields(givenParamStats, "updatedDate");

    }

    @Test
    public void testUpdate_READY() {

        val givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val givenParamStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(10L).minChangeValue(1L)
                .minValue(0L).maxValue(10L).defaultValue(1L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val expectedStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.READY)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val actual = givenParamStats.getStatus().update().apply(givenStats, givenParamStats);

        Assertions.assertThat(actual).isEqualTo(expectedStats);

    }


    @Test
    public void testUpdate_OK() {

        val givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val givenParamStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(10L).minChangeValue(1L)
                .minValue(0L).maxValue(10L).defaultValue(1L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val expectedStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.OK)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val actual = givenParamStats.getStatus().update().apply(givenStats, givenParamStats);

        Assertions.assertThat(actual).isEqualToIgnoringGivenFields(expectedStats, "updatedDate");

    }

    @Test
    public void testUpdate_EXPIRED() {

        val givenStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.EXPIRED)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("test@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val givenParamStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.EXPIRED)
                .maxChangeValue(10L).minChangeValue(1L)
                .minValue(0L).maxValue(10L).defaultValue(1L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val expectedStats = Stats.builder().id(new Stats.Id("testStats", "testService"))
                .valueType(ValueType.INT).permissionType(PermissionType.ALL).updateType(UpdateType.INCREMENT).status(StatusType.EXPIRED)
                .maxChangeValue(100L).minChangeValue(0L)
                .minValue(0L).maxValue(100L).defaultValue(0L)
                .regId("test@test.com").uptId("modify@test.com")
                .createdDate(new DateTime(1570508918000L)).updatedDate(DateTime.now())
                .build();

        val actual = givenParamStats.getStatus().update().apply(givenStats, givenParamStats);

        Assertions.assertThat(actual).isEqualToIgnoringGivenFields(expectedStats, "updatedDate");

    }

}
