package certification.testtoken.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 优化后的Jedis工具类
 * 增强资源管理、异常处理和功能完整性
 */
@Component 
public class JedisUtil {
    // 日志记录器，替代System.out
    private static final Logger log = LoggerFactory.getLogger(JedisUtil.class);

    // 单例实例
    private static volatile JedisUtil instance;

    // Jedis连接实例
    private Jedis jedis;

    // 私有化构造方法，防止外部实例化
    private JedisUtil() {
    }

    /**
     * 单例模式：双重检查锁获取实例
     * 保证线程安全且高效
     */
    public static JedisUtil getInstance() {
        if (instance == null) {
            synchronized (JedisUtil.class) {
                if (instance == null) {
                    instance = new JedisUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化连接（从连接池获取并选择数据库）
     * 
     * @return 连接成功返回true，失败返回false
     */
    public boolean setup() {
        try {
            // 从连接池获取连接（确保JedisConnectionFactory已正确配置）
            jedis = JedisConnectionFactory.getJedis();
            // 选择数据库（0号库为默认，可根据业务调整）
            jedis.select(0);
            log.info("Redis connection initialized successfully");
            return true;
        } catch (JedisConnectionException e) {
            log.error("Redis connection failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Redis initialization exception: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 存储键值对（无过期时间）
     * 
     * @param key   键（非空）
     * @param value 值（非空）
     * @return 存储成功返回true
     */
    public boolean set(String key, String value) {
        // 参数校验
        if (key == null || key.isEmpty() || value == null) {
            log.warn("Storage failed: Key or value cannot be empty");
            return false;
        }

        try {
            // 检查连接是否有效
            if (jedis == null || !jedis.isConnected()) {
                log.error("Storage failed: Redis connection not initialized or disconnected");
                return false;
            }

            // 执行SET命令
            String result = jedis.set(key, value);
            // Redis的SET命令成功返回"OK"
            boolean success = "OK".equals(result);
            log.debug("Stored key [{}] {}", key, success ? "successfully" : "failed");
            return success;
        } catch (JedisDataException e) {
            log.error("Data format error when storing (key: {}): {}", key, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Exception when storing key [{}]: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 存储键值对并设置过期时间（推荐用于Token等临时数据）
     * 
     * @param key           键
     * @param value         值
     * @param expireSeconds 过期时间（秒），必须>0
     * @return 存储成功返回true
     */
    public boolean setEx(String key, String value, int expireSeconds) {
        if (expireSeconds <= 0) {
            log.warn("Expiration time must be greater than 0");
            return false;
        }

        try {
            if (jedis == null || !jedis.isConnected()) {
                log.error("Storage failed: Redis connection not initialized or disconnected");
                return false;
            }

            // 执行SETEX命令（原子操作：存储+设置过期时间）
            String result = jedis.setex(key, expireSeconds, value);
            boolean success = "OK".equals(result);
            log.debug("Stored key [{}] (expires in {} seconds) {}", key, expireSeconds, success ? "successfully" : "failed");
            return success;
        } catch (Exception e) {
            log.error("Exception when storing key [{}] (with expiration): {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 根据键查询值并判断是否匹配
     * 
     * @param key   键
     * @param value 值
     * @return 存在且值匹配返回true，不存在、不匹配或连接异常返回false
     */
    public boolean exits(String key, String value) {
        // 参数校验是否为空
        if (key == null || key.isEmpty() || value == null) {
            log.warn("Check failed: Key or value cannot be empty");
            return false;
        }
        // 检查连接是否有效
        if (jedis == null || !jedis.isConnected()) {
            log.error("Check failed: Redis connection not initialized or disconnected");
            return false;
        }
        // 查询键值对是否存在
        String result = jedis.get(key);
        if (result == null || !result.equals(value)) {
            log.debug("Query for key [{}] failed", key);
            return false;
        }
        log.debug("Query for key [{}] succeeded", key);
        return true;
    }

    /**
     * 安全关闭连接（归还到连接池）
     * 必须在使用完毕后调用
     */
    public void close() {
        if (jedis != null) {
            try {
                // 若使用连接池，close()会将连接归还；若为单连接，则关闭TCP连接
                jedis.close();
                log.info("Redis connection released successfully");
            } catch (Exception e) {
                log.error("Exception when closing Redis connection: {}", e.getMessage());
            } finally {
                // 确保引用置空，避免重复关闭
                jedis = null;
            }
        }
    }
}