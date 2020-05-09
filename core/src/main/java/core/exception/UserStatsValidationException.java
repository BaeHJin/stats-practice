package core.exception;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserStatsValidationException extends StatsException {

    private static final Long RESULT_CODE = 300002L;

    public UserStatsValidationException(String resultMessage) {

        super(RESULT_CODE, resultMessage);

    }
}
