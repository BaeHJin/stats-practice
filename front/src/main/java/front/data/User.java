package front.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.Nullable;
import front.data.type.UserType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document(collection = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    UserId id;

    @Nullable
    ObjectId userId;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UserId {

        @NotBlank
        String id; // shard key

        @JsonProperty("user_type")
        @NotNull
        @Field(order = 1)
        UserType userType;

        @JsonProperty("member_no")
        @NotBlank
        @Field(order = 2)
        String memberNo;

        @JsonProperty("service_id")
        @NotBlank
        @Field(order = 3)
        String gameId;

        @JsonProperty("world_id")
        @Nullable
        @Field(order = 4)
        String worldId;

        @JsonProperty("character_no")
        @Nullable
        @Field(order = 5)
        String characterNo;

        public UserId(@NotNull final UserType userType, @NotNull final String memberNo, @NotNull final String gameId, @Nullable final String worldId, @Nullable final String characterNo) {

            if(userType.equals(UserType.S))
                this.id = userType+"_"+characterNo+"_"+gameId;
            else
                this.id = userType+"_"+memberNo+"_"+gameId;

            this.userType = userType;
            this.memberNo = memberNo;
            this.gameId = gameId;
            this.worldId = worldId;
            this.characterNo = characterNo;
        }
    }


    public User(UserId id) {
        this.id = id;
        this.userId =  new ObjectId();
    }


}
