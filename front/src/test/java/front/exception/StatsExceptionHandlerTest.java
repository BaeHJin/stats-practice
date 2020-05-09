package front.exception;

import lombok.val;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatsExceptionHandlerTest {

    StatsExceptionHandler handler = new StatsExceptionHandler();

    @Test
    public void TestApiResponse_case_notFoundException(){

        val exception = new NotFoundUserException("not found user");
        val response = handler.of(exception);

        assertThat(response.getCode()).isEqualTo(exception.getCode());
        assertThat(response.getMessage()).isEqualTo(exception.getMessage());

    }

    @Test
    public void TestApiResponse_case_unknownException(){

        val exception = new Exception("not found user");
        val response = handler.of(exception);

        assertThat(response.getCode()).isEqualTo(-5L);
        assertThat(response.getMessage()).isEqualTo(exception.getMessage());

    }


}
