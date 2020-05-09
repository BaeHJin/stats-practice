package front.controller;

import core.data.type.PermissionType;
import front.data.User;
import front.data.type.UserType;
import core.exception.WithExceptionHandler;
import front.ApiResponse;
import front.auth.filter.interceptor.WithUserAuth;
import front.data.Header;
import front.service.UserStatsService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@WithExceptionHandler
@Slf4j
public class UserStatsController {

    public UserStatsController(@Autowired UserStatsService service, @Autowired Header header) {
        this.service = service;
        this.header = header;
    }

    private UserStatsService service;

    Header header;

    @WithUserAuth
    @PostMapping(
            value = {
                    "/external/character/{character_no}/{stats_name}",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse updateCharacterStats(
            @PathVariable("character_no") String characterNo,
            @PathVariable("stats_name") String statsName,
            @Valid @RequestBody UpdateRequest request) throws InterruptedException {

        val response = service.put(characterNo, statsName, header.getGameId(), request.getValue(), UserType.S, PermissionType.CLIENT);

        return ApiResponse.of(response);
    }

    @WithUserAuth
    @PostMapping(
            value = {
                    "/external/member/{member_no}/{stats_name}",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse updateMemberStats(
            @PathVariable("member_no") String memberNo,
            @PathVariable("stats_name") String statsName,
            @Valid @RequestBody UpdateRequest request) throws InterruptedException {

        val response = service.put(header.getMemberNo(), statsName, header.getGameId(), request.getValue(), UserType.M, PermissionType.CLIENT);

        return ApiResponse.of(response);
    }

    @WithUserAuth
    @GetMapping(
            value = {
                    "/external/character/{character_no}/{stats_name}",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse getCharacterStats(
            @PathVariable("character_no") String characterNo,
            @PathVariable("stats_name") String statsName) {

        val response = service.get(characterNo, statsName, header.getGameId(), UserType.S);
        return ApiResponse.of(response);
    }


    @WithUserAuth
    @GetMapping(
            value = {
                    "/external/member/{member_no}/{stats_name}",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse getMemberStats(
            @PathVariable("member_no") String memberNo,
            @PathVariable("stats_name") String statsName) {

        val response = service.get(memberNo, statsName, header.getGameId(), UserType.M);

        return ApiResponse.of(response);
    }

    @PostMapping(
            value = {
                    "/internal/user/list",
            },
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    public ApiResponse getList(@Valid @RequestBody GetListRequest request) {

        val response = service.get(request.getUsers(), PageRequest.of(request.getPage(), request.getSize()));

        return ApiResponse.of(response);
    }


    @Value
    private static class GetListRequest {

        @NotNull
        List<User.UserId> users;

        int page;
        int size;

    }


    @Value
    private static class UpdateRequest {

        @NotNull
        Number value;

    }

}
