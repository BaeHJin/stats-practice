package front.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import core.data.UserStats;
import core.data.type.PermissionType;
import core.model.PagedGenericModel;
import front.data.User;
import front.data.type.UserType;
import front.data.UserStatsPayload;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import core.service.userstats.UserStatsService.SearchUserStats;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("FrontUserStatsService")
@Slf4j
public class UserStatsService {

    private UserService userService;
    private core.service.userstats.UserStatsService coreUserStatsService;
    private ApplicationEventPublisher eventPublisher;

    public UserStatsService(UserService userService, core.service.userstats.UserStatsService coreUserStatsService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.coreUserStatsService = coreUserStatsService;
        this.eventPublisher = eventPublisher;
    }

    public core.service.userstats.UserStatsService.UserStatsResponse get(String userId, String statsId, String gameId, UserType type){

        val user = userService.getOne(gameId, userId, type);

        if(user.isPresent())
            return coreUserStatsService.get(user.get().getUserId(), statsId, gameId);

        else {
            val newUser = userService.setUser(gameId, userId, type);
            return coreUserStatsService.get(newUser.getUserId(), statsId, gameId);
        }

    }

    public PagedGenericModel get(List<User.UserId> requestUsers, Pageable pageable) {

        val users = userService.get(requestUsers);

        if(ObjectUtils.isEmpty(users))
            return null;

        val userIds = userService.getUserIdsFromUsers(users);
        val serviceId = users.get(0).getId().getGameId();
        val userStats = coreUserStatsService.get(serviceId, userIds, pageable);

        return mapping(users, userStats,  pageable);

    }

    private PagedGenericModel mapping(List<User> users, Page<UserStats> userStats,  Pageable pageable) {

        List<SearchUserStatsResponse> result = Lists.newArrayList();


        for(User user : users){
            List<SearchUserStats.UserStats> userStatsByUser = Lists.newArrayList();

            List<UserStats> usersStatsOfSpecific = userStats.getContent().stream().filter(a->a.getId().getUserId().equals(user.getUserId())).collect(Collectors.toList());

            for(UserStats searchUserStats : usersStatsOfSpecific) {

                if(searchUserStats.getId().getUserId().equals(user.getUserId())) {

                    userStatsByUser.add(
                            SearchUserStats.UserStats.builder()
                            .statsId(searchUserStats.getId().getStatsId())
                            .value(searchUserStats.getValue())
                            .updatedDate(searchUserStats.getUpdatedDate().toInstant().getMillis())
                            .build()
                    );
                }

            }

            result.add(SearchUserStatsResponse.of(user, userStatsByUser));
        }

        return PagedGenericModel.of(result,
                userStats.getTotalPages(),
                userStats.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }


    public core.service.userstats.UserStatsService.UserStatsResponse put(String requestUserId, String statsId, String gameId, Number value, UserType type, PermissionType permission) throws InterruptedException {

        core.service.userstats.UserStatsService.PutResponse userStats;
        User user;

        val optionalUser = userService.getOne(gameId, requestUserId, type);

        if(optionalUser.isPresent()) {

            user = optionalUser.get();
            userStats = coreUserStatsService.put(optionalUser.get().getUserId(), statsId, gameId, value, permission);

        } else {

            user = userService.setUser(gameId, requestUserId, type);
            userStats = coreUserStatsService.put(user.getUserId(), statsId, gameId, value, permission);

        }

        eventPublisher.publishEvent(UserStatsPayload.of(user, userStats, value));

        return userStats.getUserStats();

    }


    @Getter
    @EqualsAndHashCode(callSuper = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchUserStatsResponse extends SearchUserStats{

        @JsonProperty("member_no")
        private String memberNo;

        @JsonProperty("world_id")
        private String worldId;

        @JsonProperty("character_no")
        private String characterNo;



        public SearchUserStatsResponse(User user, List<UserStats> userStats) {

            super(null, user.getId().getGameId(), userStats);
            this.memberNo = user.getId().getMemberNo();
            this.worldId = user.getId().getWorldId();
            this.characterNo = user.getId().getCharacterNo();
        }

        public static SearchUserStatsResponse of(User user, List<SearchUserStats.UserStats> userStats) {
            return new SearchUserStatsResponse(user, userStats);

        }

    }



}
