package buaazl.lock.redis.connect;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Vincent on 2018/5/10.
 */
public class RedisConnection {

    private static JedisPool jedisPool = null;

    private static JedisPool getJedisPool() {
        if(jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            //Max connection number
            config.setMaxTotal(100);
            //Max idle connection number
            config.setMaxIdle(5);
            jedisPool = new JedisPool(config, "127.0.0.1", 6379, 3000);
        }
        return jedisPool;
    }

    public static Jedis getRedisConnection() {
        return getJedisPool().getResource();
    }
}
