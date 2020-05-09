package core.service;

import core.config.RedissonConfig;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedissonConfig.class, RedisLockService.class})
public class RedisLockServiceTest {

    @Autowired
    RedisLockService redisLockService;

    @Test
    public void testLock(){

        redisLockService = new RedisLockService();
        ObjectId userId = new ObjectId();
        String statsId = "testStatsId";

        Runnable runnable1 = () -> {
            try {
                redisLockService.lock(userId, statsId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(runnable1);
        thread1.start();

    }


}
