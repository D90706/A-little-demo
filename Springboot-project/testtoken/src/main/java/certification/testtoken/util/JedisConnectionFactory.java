package certification.testtoken.util;

import java.time.Duration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Jedis;

public class JedisConnectionFactory {

    private static final JedisPool jedisPool;

    static {
        // 配置连接池信息
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWait(Duration.ofSeconds(1));
        // 创建连接池对象
        jedisPool = new JedisPool(poolConfig,"127.0.0.1", 6379);
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}
