package front.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import front.data.type.UserType;
import front.data.User;
import front.exception.InteractionException;
import front.exception.NotFoundUserException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Slf4j
@Repository
public class MemberRepository {

    static final String INTERACTION_FAILURE = "Interaction failure.";

    @Autowired
    private Internal internal;

    public User getCharacter(String gameId, String characterNo){

        val character = getCharacterInfo(gameId, characterNo);

        if(character.getReturnCode().equals(0L)) {

            return new User(new User.UserId(UserType.S, character.getMemberNo(), character.getGameId(),  character.getWorldId(), characterNo));

        } else {

            log.error("Invalid character -- gameId : {}, characterNo : {}", gameId, characterNo);
            throw new NotFoundUserException("Invalid character");

        }
    }

    protected CharacterInfoResponse getCharacterInfo(String gameId, String characterNo){


        try {
            return internal.getCharacterInfo(
                    characterNo,
                    gameId,
                    null,
                    null
            );

        } catch (Exception e) {

            log.error(INTERACTION_FAILURE, e);

            throw new InteractionException(INTERACTION_FAILURE + e.getMessage());

        }
    }


    @FeignClient(value = "member", url = "${client.mobile.host}")
    interface Internal {

        @GetMapping(
                value = {
                        "/member/v2/character/info/{characterId}"
                },
                produces = MediaType.APPLICATION_JSON_UTF8_VALUE
        )
        @Valid
        CharacterInfoResponse getCharacterInfo(
                                               @PathVariable("characterId") String characterId,
                                               @RequestParam(value = "gameId") String gameId,
                                               @RequestParam(value = "worldId") @Nullable String worldId,
                                               @RequestParam(value = "memberNo") @Nullable String memberNo
        );

    }



    @Value
    public static class CharacterInfoResponse {

        @JsonProperty("return_code")
        Long returnCode;

        @JsonProperty("return_message")
        String returnMessage;

        @JsonProperty("member_no")
        String memberNo;

        @JsonProperty("game_id")
        String gameId;

        @JsonProperty("character_id")
        String characterId;

        @JsonProperty("character_name")
        String characterName;

        @Nullable
        @JsonProperty("world_id")
        String worldId;

        @JsonProperty("character_reg_dt")
        Long characterRegDate;

        @JsonProperty("country_cd")
        String countryCode;

        @JsonProperty("provider_cd")
        String providerCode;

    }

}
