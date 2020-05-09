package front.auth.filter.interceptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import front.data.AuthContents;
import front.data.Header;
import front.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class UserAuthInterceptor extends HandlerInterceptorAdapter {

    public UserAuthInterceptor(@Autowired Header header) {
        this.header = header;
    }

    Header header;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    	if (!shouldHandle(handler))
            return true;

            String ownerContent = request.getHeader("x-owner-content");

            if (StringUtils.isEmpty(ownerContent)) {
                log.warn("x-owner-content is empty");
                throw new UnauthorizedException("Invalid Access Token");
            }

        try {

            String decodedOwnerContent = StandardCharsets.UTF_8
                    .decode(ByteBuffer.wrap(org.apache.commons.codec.binary.Base64.decodeBase64(ownerContent)))
                    .toString();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AuthContents authContents = objectMapper.readValue(decodedOwnerContent, AuthContents.class);

            log.debug("authContents : {}", authContents.toString());

            header.setAuditContents(authContents);


        } catch(IOException e) {
            log.error("AuthContents parsing error ", e);
            log.error("AuthContents parsing error ", header.toString());
            throw new UnauthorizedException("Invalid Access Token");

        }


        return true;

    }


    private boolean shouldHandle(Object handler) {
    	
        val handlerMethod = (HandlerMethod) handler;

        // method 별로 설정할 수 있도록 class 와 method 에서 WithInternalAuth 찾기
        return handlerMethod.getMethod().isAnnotationPresent(WithUserAuth.class) ||
                handlerMethod.getBeanType().isAnnotationPresent(WithUserAuth.class);

    }
}
