package front;

import core.exception.StatsException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public class ApiResponse<T> {

    public static final Long SUCCESS = 0L;
    public static final String SUCCESS_MESSAGE = "Success";

    private Long code;

    private String message;

    private T result;


    public static ApiResponse of(StatsException ae) {

        return new ApiResponse(ae.getCode(), ae.getMessage(), null);

    }

    public static ApiResponse of(Exception ae) {

        return new ApiResponse(-5L, ae.getMessage(), null);

    }


    public static <T extends Object> ApiResponse of(T response) {

        return new ApiResponse(SUCCESS, SUCCESS_MESSAGE, response);

    }

}