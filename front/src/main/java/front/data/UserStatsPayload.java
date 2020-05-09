package front.data;

import core.data.type.UpdateType;
import core.service.userstats.UserStatsService;
import front.data.type.UserType;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@Builder
@ToString
public class UserStatsPayload {

    String statsId;

    Long updatedDate;

    UpdateType updateType;

    Number previousValue;

    Number currentValue;

    Number requestValue;

    User user;

    @Id
    UUID uuid;

    @Getter
    @Builder
    @ToString
    public static class User{

        String memberNo;

        String serviceId;

        String worldId;

        String characterNo;

        String userId;

        UserType userType;
    }

    public static UserStatsPayload of(front.data.User user, UserStatsService.PutResponse userStatsResult, Number requestValue) {

        val userStats = userStatsResult.getUserStats();
        val userId = user.getId();

        return UserStatsPayload.builder()
                .statsId(userStats.getStatsId())
                .updatedDate(userStats.getUpdatedDate())
                .previousValue(userStatsResult.getPrevValue())
                .currentValue(userStats.getValue())
                .updateType(userStatsResult.getUpdateType())
                .requestValue(requestValue)
                .user(User.builder()
                        .memberNo(userId.getMemberNo())
                        .serviceId(userId.getGameId())
                        .worldId(userId.getWorldId())
                        .characterNo(userId.getCharacterNo())
                        .userId(user.getUserId().toString())
                        .userType(userId.getUserType())
                        .build())
                .uuid(UUID.randomUUID())
                .build();

    }

}

