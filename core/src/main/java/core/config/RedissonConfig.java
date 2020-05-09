package core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${redisson.hostname}")
    private String hostname;

    @Value("${redisson.port}")
    private Integer port;

    @Value("${redisson.clusterEnabled}")
    private Boolean clusterEnabled;

    @Value("${redisson.database}")
    private Integer database;

    @Value("${redisson.timeout}")
    private Integer timeout;

    @Value("${redisson.pool.maxTotal}")
    private Integer poolMaxTotal;

    @Value("${redisson.pool.maxIdle}")
    private Integer poolMaxIdle;


    @Value("${redisson.password}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {

        Config config = new Config();

        config.setCodec(new JsonJacksonCodec(mapper()));

        if (Boolean.TRUE.equals(clusterEnabled)) {

            if(!password.isEmpty()){
                config.useClusterServers().setPassword(password);
            }


            config.useClusterServers().addNodeAddress("redis://" + hostname + ":" + port);

            // redis max idle 갯수를 redission에서 min idle 갯수로 사용한다. 기본값은 10이다.
            config.useClusterServers().setMasterConnectionMinimumIdleSize(poolMaxIdle);
            config.useClusterServers().setSlaveConnectionMinimumIdleSize(poolMaxIdle);

            config.useClusterServers().setMasterConnectionPoolSize(100);
            config.useClusterServers().setSlaveConnectionPoolSize(100);

            // remote execute 서비스에서 read from replica 설정을 하면 queue에서 받았으나 hashs에 데이터가 없는 상황이 발생한다.
            config.useClusterServers().setReadMode(ReadMode.MASTER);

            config.useClusterServers().setConnectTimeout(timeout);
            config.useClusterServers().setTimeout(timeout);

            config.useClusterServers().setSubscriptionConnectionMinimumIdleSize(poolMaxIdle);
            config.useClusterServers().setSubscriptionsPerConnection(100);

            config.useClusterServers().setSubscriptionMode(SubscriptionMode.MASTER);


        } else {

            if(!password.isEmpty()){
                config.useSingleServer().setPassword(password);
            }

            config.useSingleServer().setAddress("redis://" + hostname + ":" + port);
            config.useSingleServer().setDatabase(database);

            // redis max idle 갯수를 redission에서 min idle 갯수로 사용한다. 기본값은 10이다.
            config.useSingleServer().setConnectionMinimumIdleSize(poolMaxIdle);
            config.useSingleServer().setConnectionPoolSize(100);

            config.useSingleServer().setConnectTimeout(timeout);
            config.useSingleServer().setTimeout(timeout);

            config.useSingleServer().setSubscriptionConnectionMinimumIdleSize(poolMaxIdle);
            config.useSingleServer().setSubscriptionsPerConnection(100);


        }

        return Redisson.create(config);

    }

    @Bean
    public static ObjectMapper mapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.findAndRegisterModules();

        return mapper;

    }
}
