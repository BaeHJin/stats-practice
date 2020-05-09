package core.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.data.type.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Document(collection = "stats")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Stats {


    @org.springframework.data.annotation.Id
    Stats.Id id;

    @Nullable
    String description;

    @NotNull
    ValueType valueType;

    @NotNull
    PermissionType permissionType;

    @NotNull
    UpdateType updateType;

    @NotNull
    Long maxChangeValue;

    @NotNull
    Long minChangeValue;

    @NotNull
    Long minValue;

    @NotNull
    Long maxValue;

    @NotNull
    Long defaultValue;

    @NotNull
    StatusType status;

    @NotNull
    String regId;

    @NotNull
    String uptId;

    @NotNull
    DateTime createdDate;

    @NotNull
    DateTime updatedDate;

    @Value
    public static class Id {

        @Field(order = 1) 
        String statsId;

        @Field(order = 2)
        String serviceId;
    }

}
