package front.exception;

import core.exception.StatsException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UnauthorizedException extends StatsException {

    private static final Long RESULT_CODE = 401000L;

    public UnauthorizedException(String resultMessage) {

        super(RESULT_CODE, resultMessage);

    }


}