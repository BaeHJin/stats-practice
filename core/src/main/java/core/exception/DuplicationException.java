package core.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DuplicationException extends StatsException {

    private static final Long RESULT_CODE = 409000L;

    public DuplicationException(String resultMessage) {

        super(RESULT_CODE, resultMessage);

    }

}
