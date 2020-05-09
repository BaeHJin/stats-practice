package core.service.userstats;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.data.Stats;
import core.data.UserStats;
import core.data.type.PermissionType;
import core.data.type.UpdateType;
import core.exception.UserStatsUpdateException;
import core.repository.UserStatsRepository;
import core.service.RedisLockService;
import core.service.stats.StatsCacheService;
import core.service.userstats.apply.ApplyValue;
import core.service.userstats.validation.ValidationService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserStatsService {

    private Map<String, ApplyValue> applyValueServiceMap;
    private UserStatsRepository repository;
    private ValidationService validationService;
    private StatsCacheService statsCacheService;
    private RedisLockService redisLockService;

    public UserStatsService(
                            StatsCacheService statsCacheService,
                            ValidationService validationService,
                            Map<String, ApplyValue> applyValueServiceMap,
                            UserStatsRepository repository,
                            RedisLockService redisLockService){

        this.statsCacheService = statsCacheService;
        this.validationService = validationService;
        this.applyValueServiceMap = applyValueServiceMap;
        this.repository = repository;
        this.redisLockService = redisLockService;
    }


    public UserStatsResponse get(ObjectId userId, String statsId, String serviceId){

        val stats = statsCacheService.get(statsId, serviceId);

        val result = repository.findOne(userId, statsId, serviceId);

        return UserStatsResponse.of(stats, result);

    }

    public Page<UserStats> get(String serviceId, List<ObjectId> userIds, Pageable pageable) {

        val stats = statsCacheService.get(serviceId);
        val statsIds = stats.stream().map(s->s.getId().getStatsId()).collect(Collectors.toList());
        return repository.find(userIds, serviceId, statsIds, pageable);

    }


    public PutResponse put(ObjectId userId, String statsId, String serviceId, Number value, PermissionType permission) throws InterruptedException {

        val stats = statsCacheService.get(statsId, serviceId);
        validationService.validation(stats, value, permission);

        try (RedisLockService.Lock ignored = redisLockService.lock(userId, statsId)) {
            return update(stats, userId, value);

        }

    }

    private PutResponse update(Stats stats, ObjectId userId, Number value){

        val valueService = applyValueServiceMap.get(stats.getUpdateType().getValueServiceName());

        val prevUserStats = repository.findOne(userId, stats.getId().getStatsId(), stats.getId().getServiceId());

        try {

            val appliedValue = valueService.applyValue(stats, value, prevUserStats);
            val result = repository.update(userId, stats.getId().getStatsId(), stats.getId().getServiceId(), appliedValue, stats.getUpdateType());

            return PutResponse.of(stats, prevUserStats, result);

        } catch (UserStatsUpdateException e){

            return PutResponse.of(stats, prevUserStats);

        }

    }


    @Data
    @Builder
    @AllArgsConstructor
    public static class SearchUserStats{

        @JsonProperty("user_id")
        ObjectId userId;
        @JsonProperty("service_id")
        String serviceId;
        @JsonProperty("user_stats")
        List<UserStats> userStats;

        @Builder
        @ToString
        public static class UserStats{

            @JsonProperty("stats_name")
            String statsId;

            @JsonProperty("value")
            Number value;

            @JsonProperty("updated_date")
            Long updatedDate;

        }

    }


    @Getter
    @Builder
    public static class PutResponse{

        UserStatsResponse userStats;
        Number prevValue;
        UpdateType updateType;

        public static PutResponse of(Stats stats, Optional<UserStats> prevUserStats, UserStats userStats) {

            val prevVal = prevUserStats.isPresent() ? prevUserStats.get().getValue() : stats.getDefaultValue();

            return PutResponse.builder()
                    .prevValue(prevVal)
                    .userStats(UserStatsResponse.of(userStats))
                    .updateType(stats.getUpdateType())
                    .build();

        }

        public static PutResponse of(Stats stats, Optional<UserStats> prevUserStats) {

            val prevVal = prevUserStats.isPresent() ? prevUserStats.get().getValue() : stats.getDefaultValue();

            return PutResponse.builder()
                    .prevValue(prevVal)
                    .userStats(UserStatsResponse.of(stats, prevUserStats))
                    .updateType(stats.getUpdateType())
                    .build();

        }

    }

    @Value
    @EqualsAndHashCode
    public static class UserStatsResponse{

        @JsonProperty("stats_name")
        String statsId;
        Number value;
        @JsonProperty("updated_date")
        Long updatedDate;

        public static UserStatsResponse of(Stats stats, Optional<UserStats> userStatsOptional) {

            if (userStatsOptional.isPresent()) {
                val userStats = userStatsOptional.get();
                return new UserStatsResponse(
                        userStats.getId().getStatsId(),
                        stats.getValueType().getValue().applyAsLong(userStats.getValue()),
                        userStats.getUpdatedDate().toInstant().getMillis());
            } else
                return new UserStatsResponse(
                        stats.getId().getStatsId(),
                        stats.getDefaultValue(),
                        System.currentTimeMillis());
        }

        public static UserStatsResponse of(UserStats userStats) {

            return new UserStatsResponse(
                    userStats.getId().getStatsId(),
                    userStats.getValue(),
                    userStats.getUpdatedDate().toInstant().getMillis());
        }



    }


}
