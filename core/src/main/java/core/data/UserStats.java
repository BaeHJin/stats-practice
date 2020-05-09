package core.data;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "userStats")
@EqualsAndHashCode
public class UserStats {

    @Id
    UserStatsId id; // shard key

    Long value;

    @NotNull
    DateTime updatedDate;


    @Value
    public static class UserStatsId {

        @Field(order = 1)
        ObjectId userId;
        @Field(order = 2)
        String statsId;
        @Field(order = 3 )
        String serviceId;

    }
}