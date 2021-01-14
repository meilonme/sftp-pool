package me.meilon.sftp.core.conf;



import lombok.Setter;
import lombok.experimental.Accessors;
import me.meilon.sftp.core.SftpConnect;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;


/**
 * @author meilon
 * 链接池的基础配置
 */
public class SftpPoolConfig extends GenericKeyedObjectPoolConfig<SftpConnect> {

    public static Builder builder(){
        return new Builder();
    }

    public GenericKeyedObjectPoolConfig<SftpConnect> toGenericKeyedObjectPoolConfig(){
        // 强制设置获取和归还对象时进行可用性校验
        setTestOnBorrow(true);
        setTestOnReturn(true);
        setBlockWhenExhausted(true);
        return this;
    }

    @Setter
    @Accessors(fluent=true,chain=true)
    public static class Builder{

        /**
         * 连接池放池对象的方式，true：放在空闲队列最前面，false：放在空闲队列最后面，默认为 true
         */
        private boolean lifo = DEFAULT_LIFO;

        /**
         * 从池中获取/返还对象时是否使用公平锁机制，默认为 false
         */
        private boolean fairness = DEFAULT_FAIRNESS;

        /**
         * 获取资源的等待时间。blockWhenExhausted 为 true 时有效。-1 代表无时间限制，一直阻塞直到有可用的资源
         */
        private long maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;


        private long evictorShutdownTimeoutMillis = DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;

        /**
         * 对象空闲的最小时间，达到此值后空闲对象将可能会被移除。
         * 不受最小连接数限制影响
         * -1 表示不移除；默认 30 分钟
         */
        private long minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

        /**
         * 连接空闲的最小时间，达到此值后空闲链接将会被移除，
         * 但会保留最小空闲连接数
         * 默认为-1.
         */
        private long softMinEvictableIdleTimeMillis = DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

        /**
         * “空闲链接”检测线程每次检测的资源数, 默认为 3.
         */
        private int numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

        private EvictionPolicy<SftpConnect> evictionPolicy = null; // Only 2.6.0 applications set this

        /**
         * 资源回收策略，默认值 {@link org.apache.commons.pool2.impl.DefaultEvictionPolicy}
         */
        private String evictionPolicyClassName = DEFAULT_EVICTION_POLICY_CLASS_NAME;

        /**
         * 创建对象时是否调用 factory.validateObject 方法验证资源有效性，
         * 默认 false
         */
        private boolean testOnCreate = DEFAULT_TEST_ON_CREATE;

        /**
         * 取出对象时是否调用 factory.validateObject 方法验证资源有效性，
         * 默认 false
         */
        private boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;

        /**
         * 返还对象时是否调用 factory.validateObject 方法验证资源有效性，
         * 默认 false
         */
        private boolean testOnReturn = DEFAULT_TEST_ON_RETURN;

        /**
         * 池中的闲置对象是否由逐出器验证。无法验证的对象将从池中删除销毁。
         * 默认 false
         */
        private boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;

        /**
         * “空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。
         * 默认值 -1L
         */
        private long timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

        /**
         * 资源耗尽时，是否阻塞等待获取资源，默认 true
         */
        private boolean blockWhenExhausted = DEFAULT_BLOCK_WHEN_EXHAUSTED;

        private boolean jmxEnabled = DEFAULT_JMX_ENABLE;

        // TODO Consider changing this to a single property for 3.x
        private String jmxNamePrefix = DEFAULT_JMX_NAME_PREFIX;

        private String jmxNameBase = DEFAULT_JMX_NAME_BASE;

        /**
         * 每个key最小保持的空闲链接数, 默认 1
         */
        private int minIdlePerKey = 1;

        /**
         * 每个key最大保持的空闲链接数, 默认 8
         */
        private int maxIdlePerKey = DEFAULT_MAX_IDLE_PER_KEY;

        /**
         * 每个key最大可存在的链接数, 默认 8
         */
        private int maxTotalPerKey = DEFAULT_MAX_TOTAL_PER_KEY;

        /**
         * 链接池中最大连接数，默认值 8
         */
        private int maxTotal = DEFAULT_MAX_TOTAL;


        public SftpPoolConfig build(){
            SftpPoolConfig config = new SftpPoolConfig();
            config.setMaxTotal(maxTotal);
            config.setMaxIdlePerKey(maxIdlePerKey);
            config.setMinIdlePerKey(minIdlePerKey);
            config.setMaxTotalPerKey(maxTotalPerKey);
            config.setLifo(lifo);
            config.setFairness(fairness);
            config.setMaxWaitMillis(maxWaitMillis);
            config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            config.setEvictorShutdownTimeoutMillis(evictorShutdownTimeoutMillis);
            config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
            config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
            config.setEvictionPolicy(evictionPolicy);
            config.setEvictionPolicyClassName(evictionPolicyClassName);
            config.setTestOnCreate(testOnCreate);
            config.setTestOnBorrow(testOnBorrow);
            config.setTestOnReturn(testOnReturn);
            config.setBlockWhenExhausted(blockWhenExhausted);
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            if (timeBetweenEvictionRunsMillis > 0){
                this.testWhileIdle = true;
            }
            config.setTestWhileIdle(testWhileIdle);
            config.setJmxEnabled(jmxEnabled);
            config.setJmxNamePrefix(jmxNamePrefix);
            config.setJmxNameBase(jmxNameBase);
            return config;
        }

    }

}
