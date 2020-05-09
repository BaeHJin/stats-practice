package core.service;

import lombok.val;
import org.bson.types.ObjectId;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    @Autowired
    private RedissonClient redissonClient;

    public static final String REDIS_LOCK_PREFIX = "STATS_LOCK";

    public Lock lock(ObjectId userId, String statsId) throws InterruptedException {

        val rlock =  redissonClient.getLock(createLockKey(statsId, userId));
        val result = rlock.tryLock(3, 2, TimeUnit.SECONDS);

        if (!result)
            throw new IllegalStateException("Failure to accuire lock for "+statsId +":"+userId);
        return new Lock(rlock);

    }

    private String createLockKey(String statsId, ObjectId userId){
        StringJoiner sj = new StringJoiner(":");
        sj.add(REDIS_LOCK_PREFIX);
        sj.add(statsId);
        sj.add(userId.toString());

        return sj.toString();
    }

    public static class Lock implements AutoCloseable{

        private RLock rLock;

        public Lock(RLock rLock){

            this.rLock = rLock;

        }

        @Override
        public void close() {

            if (this.rLock != null)
                this.rLock.unlock();

        }

    }



}