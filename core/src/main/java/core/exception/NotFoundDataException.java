package core.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotFoundDataException extends StatsException {

    private static final Long RESULT_CODE = 404000L;

    public NotFoundDataException(String resultMessage) {

        super(RESULT_CODE, resultMessage);

    }

}
