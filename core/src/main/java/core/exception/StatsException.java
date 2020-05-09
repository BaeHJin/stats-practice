package core.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@EqualsAndHashCode(callSuper = true)
@NonFinal
public class StatsException extends RuntimeException {

    private final  Long code;

    private final  String message;

    public StatsException(Long code, String message) {

        super(message);

        this.code = code;

        this.message = message;

    }

}
