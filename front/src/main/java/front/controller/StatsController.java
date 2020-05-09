package front.controller;

import core.data.Stats;
import core.data.type.*;
import core.exception.WithExceptionHandler;
import core.service.stats.StatsService;
import front.ApiResponse;
import front.data.type.UserType;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@WithExceptionHandler
@Slf4j
public class StatsController {

    private StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @PutMapping(
            value = {
                    "/internal/metadata/create",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse put(
            @Valid @RequestBody StatsRequest request) {

        val response = service.insert(StatsRequest.toStats(request));

        return ApiResponse.of(response);
    }

    @PutMapping(
            value = {
                    "/internal/metadata/update",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse update(
            @Valid @RequestBody StatsRequest request) {

        val response = service.update(StatsRequest.toStats(request));

        return ApiResponse.of(response);
    }

    @PostMapping(
            value = {
                    "/internal/metadata/delete",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse delete(
            @RequestParam(value = "serviceId") String serviceId,
            @RequestParam(value = "statsName") Set<String> statsName) {

        val response = service.delete(serviceId, statsName);

        return ApiResponse.of(response);
    }

    @GetMapping(
            value = {
                    "/internal/metadata/list",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse getList(
            @RequestParam(value = "serviceId") String serviceId,
            @RequestParam(required = false, value = "statsName") String statsName,
            @RequestParam(required = false, value = "status") StatusType status,
            @RequestParam(required = false, value = "title") String title,
            Pageable pageable) {

        log.debug("pageable.page : {}, pageable.size : {}, pageable.sort: {}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        val response = service.get(serviceId, statsName, status, title, pageable);

        return ApiResponse.of(response);
    }

    @GetMapping(
            value = {
                    "/internal/metadata/duplication",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse duplication(
            @RequestParam(value = "serviceId") String serviceId,
            @RequestParam(required = false, value = "statsName") String statsName) {

        val response = service.duplicate(statsName, serviceId);

        return ApiResponse.of(response);
    }


    @Value
    public static class StatsRequest {

        @NotBlank String statsName;
        @NotBlank String serviceId;
        @Nullable String description;
        @NotBlank String adminId;
        @NotNull ValueType valueType;
        @Nullable UserType userType;
        @NotNull PermissionType permissionType;
        @NotNull UpdateType updateType;
        @NotNull Long maxChangeValue;
        @NotNull Long minChangeValue;
        @NotNull Long minValue;
        @NotNull Long maxValue;
        @NotNull Long defaultValue;
        @NotNull StatusType status;

        public static Stats toStats(StatsRequest request){
            val date = DateTime.now();
            return Stats.builder()
                    .id(new Stats.Id(request.getStatsName(), request.getServiceId()))
                    .description(request.getDescription())
                    .valueType(request.getValueType())
                    .permissionType(request.getPermissionType())
                    .updateType(request.getUpdateType())
                    .maxChangeValue(request.getMaxChangeValue())
                    .minChangeValue(request.getMinChangeValue())
                    .minValue(request.getMinValue())
                    .maxValue(request.getMaxValue())
                    .defaultValue(request.getDefaultValue())
                    .status(request.getStatus())
                    .regId(request.getAdminId())
                    .uptId(request.getAdminId())
                    .createdDate(date)
                    .updatedDate(date)
                    .build();

        }

    }

}
