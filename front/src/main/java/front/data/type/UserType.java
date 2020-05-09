package front.data.type;

import front.data.User;
import lombok.Getter;
import java.util.function.Function;

@Getter
public enum UserType {
    M("M", "_id.memberNo", userId -> userId.getUserType()+"_"+userId.getMemberNo()+"_"+userId.getGameId()),

    S("S", "_id.characterNo", userId -> userId.getUserType()+"_"+userId.getCharacterNo()+"_"+userId.getGameId());

    private String code;
    private String queryId;
    private Function<User.UserId, String> f1;

    UserType(String code, String queryId, Function<User.UserId, String> f1) {
        this.code = code;
        this.queryId = queryId;
        this.f1 = f1;
    }

    public Function<User.UserId, String> getId(){return f1;}



}
