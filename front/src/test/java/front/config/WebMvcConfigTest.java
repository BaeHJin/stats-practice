package front.config;

import front.auth.filter.interceptor.UserAuthInterceptor;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebMvcConfigTest {

    @Mock
    private UserAuthInterceptor userAuthInterceptor;

    @InjectMocks
    private WebMvcConfig webMvcConfig = new WebMvcConfig();

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddInterceptors(){

        InterceptorRegistry registry = new InterceptorRegistry();
        webMvcConfig.addInterceptors(registry);

        List<Object> result = ReflectionTestUtils.invokeMethod(registry, "getInterceptors");
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testAddInterceptors_case_userAuthInterceptor_is_null(){

        webMvcConfig = new WebMvcConfig();

        InterceptorRegistry registry = new InterceptorRegistry();
        webMvcConfig.addInterceptors(registry);

        List<Object> result = ReflectionTestUtils.invokeMethod(registry, "getInterceptors");
        assertThat(result.size()).isEqualTo(0);
    }

}
