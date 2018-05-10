package buaazl.lock.redis;

import buaazl.lock.DistributedLock;
import buaazl.lock.redis.connect.RedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by Vincent on 2018/4/22.
 */
public class RedisDtLock extends DistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisDtLock.class);

    private String key;

    private String requestId;

    private final RedisConnection redisConnection;

    public RedisDtLock(RedisConnection redisConnection, String key, String requestId) {
        this.redisConnection = redisConnection;
        this.key = key;
        this.requestId = requestId;
    }

    public void lock() {


    }

    public boolean tryLock(long millisTimeout) {
        long startTime = System.currentTimeMillis();
        long sleepTime = millisTimeout / 10;
        long expireTime;
        Jedis redisConnection = RedisConnection.getRedisConnection();
        for(;;) {
            expireTime = System.currentTimeMillis() + millisTimeout + 1;
            //Set the requestId as value, to identify the client
            String result = redisConnection.set(key, requestId, RedisDtLockConstant.SET_IF_NOT_EXIST, RedisDtLockConstant.SET_WITH_EXPIRE_TIME, expireTime);
            if(RedisDtLockConstant.LOCK_SUCCESS.equals(result)) {
                return true;
            } else {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(isExpired(startTime, millisTimeout))
                break;
        }
        return false;
    }

    private boolean isExpired(long startTime, long timeout) {
        if(System.currentTimeMillis() - startTime > timeout) {
            return true;
        }
        return false;
    }
}
