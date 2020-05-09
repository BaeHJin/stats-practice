package front;

import com.github.mwiede.feign.validation.ExtendedFeign;
import feign.Feign;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.validation.Validator;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Configuration
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
public class FeignConfig {

    @Bean
    @Scope("prototype")
    public Feign.Builder validatableFeignBuilder(Validator validator) {

        return ExtendedFeign.builder(validator)
                .logLevel(Logger.Level.FULL);

    }


}
