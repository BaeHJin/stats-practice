package core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class App {

    public static void main(String[] args) {

        new SpringApplicationBuilder(App.class).properties("spring.config.name:application,core,front").build().run(args);

    }

}
