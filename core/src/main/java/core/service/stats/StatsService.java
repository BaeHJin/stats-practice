package core.service.stats;

import core.data.Stats;
import core.data.type.*;
import core.exception.DuplicationException;
import core.exception.NotFoundDataException;
import core.model.PagedGenericModel;
import core.repository.StatsRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class StatsService {

    public StatsService(StatsRepository statsRepository){
        this.statsRepository = statsRepository;
    }

    private StatsRepository statsRepository;

    private static final String NOT_FOUNT_STATS_MESSAGE = "Not Found Stats - statsId : {}, serviceId : {}";

    public PagedGenericModel get(String serviceId, @Nullable String statsId, @Nullable StatusType status, @Nullable  String title, Pageable pageable){

        val stats = statsRepository.find(serviceId, statsId, status, title, pageable);

        return PagedGenericModel.of(StatsResult.of(stats.getContent()),
                stats.getTotalPages(),
                stats.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    public Stats get(String statsId, String serviceId){

        val stats = statsRepository.findOne(statsId, serviceId);

        if(stats.isPresent())
            return stats.get();

        else {
            log.error(NOT_FOUNT_STATS_MESSAGE, statsId, serviceId);
            throw new NotFoundDataException("Not found stats");
        }

    }

    public StatsResult insert(Stats stats){

        try {

            return StatsResult.of(statsRepository.insert(stats));

        } catch (DuplicateKeyException e){

            log.error("Duplication error. key : " + e, stats.getId());
            throw new DuplicationException("Stats key is Duplication.");

        }

    }

    public StatsResult update(Stats stats){

        val prevStats = statsRepository.findOne(stats.getId().getStatsId(), stats.getId().getServiceId());

        if(!prevStats.isPresent()) {
            log.error(NOT_FOUNT_STATS_MESSAGE, stats.getId().getStatsId(), stats.getId().getServiceId());
            throw new NotFoundDataException("Not found stats");
        }

        val updateStats = stats.getStatus().update().apply(prevStats.get(), stats);

        return StatsResult.of(statsRepository.update(updateStats));


    }

    public boolean duplicate(String statsId, String serviceId){

        val stats = statsRepository.findOne(statsId, serviceId);

        if(stats.isPresent())
            return true;

        else {
            log.error(NOT_FOUNT_STATS_MESSAGE, statsId, serviceId);
            return false;
        }

    }

    public Set<String> delete(String serviceId, Set<String> statsNames) {

        val statsIds = statsNames.stream().map( id -> new Stats.Id(id, serviceId) ).collect(Collectors.toSet());

        return statsRepository.delete(statsIds);
    }


    @Builder
    @EqualsAndHashCode
    @ToString
    @Getter
    public static class StatsResult {

        String statsId;
        String serviceId;
        String statsName;
        String description;
        ValueType valueType;
        PermissionType permissionType;
        UpdateType updateType;
        Long maxChangeValue;
        Long minChangeValue;
        Long minValue;
        Long maxValue;
        Long defaultValue;
        StatusType status;
        String regId;
        String uptId;
        Long createdDate;
        Long updatedDate;

        public static List<StatsResult> of(List<Stats> statsLilst){

            return statsLilst.stream().map( stats -> StatsResult.builder()
                    .statsId(stats.getId().getServiceId()+"|"+stats.getId().getStatsId())
                    .serviceId(stats.getId().getServiceId())
                    .statsName(stats.getId().getStatsId())
                    .description(stats.getDescription())
                    .valueType(stats.getValueType())
                    .permissionType(stats.getPermissionType())
                    .updateType(stats.getUpdateType())
                    .maxChangeValue(stats.getMaxChangeValue())
                    .minChangeValue(stats.getMinChangeValue())
                    .minValue(stats.getMinValue())
                    .maxValue(stats.getMaxValue())
                    .defaultValue(stats.getDefaultValue())
                    .status(stats.getStatus())
                    .regId(stats.getRegId())
                    .uptId(stats.getUptId())
                    .createdDate(stats.getCreatedDate().toInstant().getMillis())
                    .updatedDate(stats.getUpdatedDate().toInstant().getMillis())
                    .build()).collect(Collectors.toList());


        }

        public static StatsResult of(Stats stats){

            return StatsResult.builder()
                    .statsId(stats.getId().getServiceId()+"|"+stats.getId().getStatsId())
                    .serviceId(stats.getId().getServiceId())
                    .statsName(stats.getId().getStatsId())
                    .description(stats.getDescription())
                    .valueType(stats.getValueType())
                    .permissionType(stats.getPermissionType())
                    .updateType(stats.getUpdateType())
                    .maxChangeValue(stats.getMaxChangeValue())
                    .minChangeValue(stats.getMinChangeValue())
                    .minValue(stats.getMinValue())
                    .maxValue(stats.getMaxValue())
                    .defaultValue(stats.getDefaultValue())
                    .status(stats.getStatus())
                    .regId(stats.getRegId())
                    .uptId(stats.getUptId())
                    .createdDate(stats.getCreatedDate().toInstant().getMillis())
                    .updatedDate(stats.getUpdatedDate().toInstant().getMillis())
                    .build();

        }
    }

}
