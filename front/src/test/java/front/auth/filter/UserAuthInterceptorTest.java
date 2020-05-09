package front.auth.filter;

import front.auth.filter.interceptor.UserAuthInterceptor;
import front.auth.filter.interceptor.WithUserAuth;
import front.data.Header;
import front.exception.UnauthorizedException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.method.HandlerMethod;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UserAuthInterceptorTest {

    private UserAuthInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Header header;
    private String headerToken = "ewogICAgInNpZCI6ICJTVE9WRV9URVNURVIiLAogICAgImRpZCI6ICIxNTc5NTA4NzYxMTY4IiwKICAgICJ0eXAiOiAibWVtYmVyIiwKICAgICJzdWIiOiAiezE1Nzk1MTcyMzYwMzB9U1RPVkVfVEVTVEVSMTU3OTU2OTUxMjk5NCIsCiAgICAicGxkIjogewogICAgICAgICJuaWNrbmFtZSI6ICJzdGF0c1Rlc3QiLAogICAgICAgICJyZWdfZHQiOiAxNTc5NTE3MjM2MDMwLAogICAgICAgICJjb3VudHJ5X2NkIjogIktSIiwKICAgICAgICAidXRjX29mZnNldCI6IDU0MCwKICAgICAgICAicHJvZmlsZV9pbWdfdXJsIjogIjE1Nzk1MTcyMzYwMzBfcHJvZmlsZV9pbWciLAogICAgICAgICJ0aW1lem9uZSI6ICJBc2lhXC9TZW91bCIsCiAgICAgICAgIm1lbWJlcl9ubyI6IDEyMzQsCiAgICAgICAgImxhbmd1YWdlX2NkIjogIktPIgogICAgfSwKICAgICJhdWQiOiAidGVzdGdhbWVfaW9zMiIsCiAgICAiZXhwIjogMTU3OTU5MTExMjk5NCwKICAgICJwY2QiOiAiVEVTVCIKfQ==";

    @Before
    public void setUp(){

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        header = mock(Header.class);

        interceptor = new UserAuthInterceptor(header);

    }


    @WithUserAuth
    class TestUserController {
        public void testMethod() {}
    }


    class TestUserController2 {
        @WithUserAuth
        public void testMethod() {}
    }

    class TestStatsController {
        public void testMethod() {}
    }


    @Test
    public void testPreHandleShouldNotHandle() throws NoSuchMethodException {

        //given

        Method method = TestStatsController.class.getMethod("testMethod");
        TestStatsController controller = new TestStatsController();
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        //when
        val actual = interceptor.preHandle(request, response, handlerMethod);

        //then
        Assertions.assertThat(actual).isTrue();


    }

    @Test
    public void testPreHandle() throws NoSuchMethodException {

        //given

        Method method = TestUserController.class.getMethod("testMethod");
        TestUserController controller = new TestUserController();
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);


        when(request.getHeader("x-owner-content")).thenReturn(headerToken);

        //when
        val actual = interceptor.preHandle(request, response, handlerMethod);

        //then
        Assertions.assertThat(actual).isTrue();


    }

    @Test
    public void testPreHandleMethod() throws NoSuchMethodException {

        //given

        Method method = TestUserController2.class.getMethod("testMethod");
        TestUserController2 controller = new TestUserController2();
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);


        when(request.getHeader("x-owner-content")).thenReturn(headerToken);

        //when
        val actual = interceptor.preHandle(request, response, handlerMethod);

        //then
        Assertions.assertThat(actual).isTrue();

    }


    @Test(expected = UnauthorizedException.class)
    public void testPreHandleTokenIsNull() throws NoSuchMethodException {

        //given

        Method method = TestUserController.class.getMethod("testMethod");
        TestUserController controller = new TestUserController();
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        when(request.getHeader("x-owner-content")).thenReturn(null);

        //when
        interceptor.preHandle(request, response, handlerMethod);

        //then
    }


    @Test(expected = UnauthorizedException.class)
    public void testPreHandleCommonSessionNotSuccess() throws NoSuchMethodException {

        //given

        Method method = TestUserController.class.getMethod("testMethod");
        TestUserController controller = new TestUserController();
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        when(request.getHeader("x-owner-content")).thenReturn("accessToken");

        //when
        interceptor.preHandle(request, response, handlerMethod);

        //then

    }


}
