package front.exception;

import core.exception.StatsException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InteractionException extends StatsException {

    private static final Long RESULT_CODE = -5L;

    public InteractionException(String resultMessage) {

        super(RESULT_CODE, resultMessage);

    }

}