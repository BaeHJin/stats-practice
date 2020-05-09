package core.config;

import lombok.val;
import org.junit.Test;
import org.redisson.client.RedisConnectionException;
import org.springframework.test.util.ReflectionTestUtils;


public class RedissonConfigTest {


    //todo Test assertion 부분을 좀 더 고려해보쟈!
    //임시로 exception 처리 되어있다

    @Test(expected = RedisConnectionException.class)
    public void testConfig_clusterEnabled_false_and_password(){

        //given
        val redissonConfig = new RedissonConfig();
        ReflectionTestUtils.setField(redissonConfig, "hostname", "test-host");
        ReflectionTestUtils.setField(redissonConfig, "port", 6379);
        ReflectionTestUtils.setField(redissonConfig, "clusterEnabled", false);
        ReflectionTestUtils.setField(redissonConfig, "database", 10);
        ReflectionTestUtils.setField(redissonConfig, "timeout", 1000);
        ReflectionTestUtils.setField(redissonConfig, "poolMaxIdle", 10);
        ReflectionTestUtils.setField(redissonConfig, "password", "test");

        //when
        redissonConfig.redisson();

    }

    @Test(expected = Exception.class)
    public void testConfig_clusterEnabled_true_and_password_is_empty(){

        //given
        val redissonConfig = new RedissonConfig();
        ReflectionTestUtils.setField(redissonConfig, "hostname", "test-host");
        ReflectionTestUtils.setField(redissonConfig, "port", 6379);
        ReflectionTestUtils.setField(redissonConfig, "clusterEnabled", true);
        ReflectionTestUtils.setField(redissonConfig, "database", 10);
        ReflectionTestUtils.setField(redissonConfig, "timeout", 1000);
        ReflectionTestUtils.setField(redissonConfig, "poolMaxIdle", 10);
        ReflectionTestUtils.setField(redissonConfig, "password", "");

        //when
        redissonConfig.redisson();

    }

    @Test(expected = RedisConnectionException.class)
    public void testConfig_clusterEnabled_true_and_password(){

        //given
        val redissonConfig = new RedissonConfig();
        ReflectionTestUtils.setField(redissonConfig, "hostname", "test-host");
        ReflectionTestUtils.setField(redissonConfig, "port", 6379);
        ReflectionTestUtils.setField(redissonConfig, "clusterEnabled", true);
        ReflectionTestUtils.setField(redissonConfig, "database", 10);
        ReflectionTestUtils.setField(redissonConfig, "timeout", 1000);
        ReflectionTestUtils.setField(redissonConfig, "poolMaxIdle", 10);
        ReflectionTestUtils.setField(redissonConfig, "password", "test");

        //when
        redissonConfig.redisson();

    }

}
