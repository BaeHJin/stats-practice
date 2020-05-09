package front.exception;

import core.exception.StatsException;
import core.exception.WithExceptionHandler;
import front.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = WithExceptionHandler.class)
public class StatsExceptionHandler {

    private static final Logger ExceptionLog = LoggerFactory.getLogger("EXCEPTION_LOG");

    @ExceptionHandler(StatsException.class)
    @ResponseBody
    public ApiResponse of(StatsException se) {
        ExceptionLog.warn(se.getMessage());
        return ApiResponse.of(se);

    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse of(Exception se) {
        ExceptionLog.error(se.getMessage());
        return ApiResponse.of(se);

    }

}
