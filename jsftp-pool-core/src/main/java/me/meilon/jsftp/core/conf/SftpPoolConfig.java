package me.meilon.jsftp.core.conf;


import lombok.Getter;
import lombok.Setter;
import me.meilon.jsftp.core.SftpEvictionPolicy;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;


/**
 * @author meilon
 * 链接池的基础配置
 */
@Getter
@Setter
public class SftpPoolConfig {

    /**
     * 连接池放池对象的方式，
     * true：放在空闲队列最前面，
     * false：放在空闲队列最后面，
     * 默认为 true
     */
    private boolean lifo = BaseObjectPoolConfig.DEFAULT_LIFO;

    /**
     * 从池中获取/返还对象时是否使用公平锁机制，
     * 默认 false
     */
    private boolean fairness = BaseObjectPoolConfig.DEFAULT_FAIRNESS;

    /**
     * 获取资源的等待时间。
     * {@link #setBlockWhenExhausted } 为 true 时有效。
     * -1 代表无时间限制，一直阻塞直到有可用的资源
     * 默认 -1
     */
    private long maxWaitMillis = BaseObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;


    /**
     * 链接关闭的超时时间, 毫秒值
     * 默认 10_000 毫秒
     */
//    private long evictorShutdownTimeoutMillis = BaseObjectPoolConfig.DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;

    /**
     * 对象空闲的最小时间，达到此值后空闲对象将可能会被移除。
     * 不受最小连接数限制影响
     * 有效性验证通过 {@link me.meilon.jsftp.core.SftpPooledFactory#validateObject(String, PooledObject)} 方法执行
     * -1 表示不移除.
     * 默认 30 分钟
     */
    private long minEvictableIdleTimeMillis = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * 连接空闲的最小时间，达到此值后空闲链接将会被移除，
     * 但会保留最小空闲连接数
     * 有效性验证通过 {@link me.meilon.jsftp.core.SftpPooledFactory#validateObject(String, PooledObject)} 方法执行
     * 默认为-1.
     */
    private long softMinEvictableIdleTimeMillis = BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * “空闲链接”检测线程每次检测链接有效性时抽查的资源数;
     * 有效性验证通过 {@link me.meilon.jsftp.core.SftpPooledFactory#validateObject(String, PooledObject)} 方法执行
     * 默认 3.
     */
    private int numTestsPerEvictionRun = BaseObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    /**
     * 资源回收策略，
     * 可通过实现 {@link EvictionPolicy} 接口实现自定义回收策略
     * 默认值 {@link SftpEvictionPolicy}
     */
    private String evictionPolicyClassName = SftpEvictionPolicy.class.getName();

    /**
     * 创建对象时是否验证资源有效性
     * 有效性验证通过 {@link me.meilon.jsftp.core.SftpPooledFactory#validateObject(String, PooledObject)} 方法执行
     * 默认 false
     */
    private boolean testOnCreate = BaseObjectPoolConfig.DEFAULT_TEST_ON_CREATE;

    /**
     * 设为正整数, 表示对池中空闲链接进行有效性校验的周期，毫秒数。
     * 如果为负值，表示不运行“检测线程”。
     * 有效性验证是通过 {@link me.meilon.jsftp.core.SftpPooledFactory#validateObject(String, PooledObject)} 方法执行的
     * 默认值 -1L
     */
    private long timeBetweenEvictionRunsMillis = BaseObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    /**
     * 资源耗尽时，是否阻塞等待获取资源，
     * 默认 true
     */
    private boolean blockWhenExhausted = BaseObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

    /**
     * 每个key最小保持的空闲链接数.
     * 默认 1
     * @see #setSoftMinEvictableIdleTimeMillis(long)
     * @see #setMinEvictableIdleTimeMillis(long)
     */
    private int minIdlePerKey = 1;

    /**
     * 每个key最大保持的空闲链接数;
     * 默认 8
     */
    private int maxIdlePerKey = GenericKeyedObjectPoolConfig.DEFAULT_MAX_IDLE_PER_KEY;

    /**
     * 每个key最大可存在的链接数;
     * 默认 8
     */
    private int maxTotalPerKey = GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY;

    /**
     * 链接池中总体可存放的最大连接数，
     * 实际使用中, 只要连接数不超过 maxTotalPerKey 依然可以正常创建新链接，
     * 但是超出后创建的链接使用完就会销毁
     * 设置为负值表示不限制
     * 默认值 -1
     */
    private int maxTotal = GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL;



    public static Builder builder(){
        return new Builder();
    }

    public GenericKeyedObjectPoolConfig toGenericKeyedObjectPoolConfig(){
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdlePerKey(maxIdlePerKey);
        config.setMinIdlePerKey(minIdlePerKey);
        config.setMaxTotalPerKey(maxTotalPerKey);
        config.setLifo(lifo);
        config.setFairness(fairness);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
//        config.setEvictorShutdownTimeoutMillis(evictorShutdownTimeoutMillis);
        config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        config.setEvictionPolicyClassName(evictionPolicyClassName);
        config.setTestOnCreate(testOnCreate);
        config.setBlockWhenExhausted(blockWhenExhausted);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        config.setTestWhileIdle(timeBetweenEvictionRunsMillis > 0);
        // 默认设置获取和归还对象时进行可用性校验
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        return config;
    }


    public static class Builder{

        private final SftpPoolConfig config = new SftpPoolConfig();

        public Builder setLifo(boolean lifo) {
            config.setLifo(lifo);
            return this;
        }

        public Builder setFairness(boolean fairness) {
            config.setFairness(fairness);
            return this;
        }

        public Builder setMaxWaitMillis(long maxWaitMillis) {
            config.setMaxWaitMillis(maxWaitMillis);
            return this;
        }

//        public Builder setEvictorShutdownTimeoutMillis(long evictorShutdownTimeoutMillis) {
//            config.setEvictorShutdownTimeoutMillis(evictorShutdownTimeoutMillis);
//            return this;
//        }

        public Builder setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
            config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            return this;
        }

        public Builder setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
            config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
            return this;
        }

        public Builder setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
            config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
            return this;
        }

        public Builder setEvictionPolicyClassName(String evictionPolicyClassName) {
            config.setEvictionPolicyClassName(evictionPolicyClassName);
            return this;
        }

        public Builder setTestOnCreate(boolean testOnCreate) {
            config.setTestOnCreate(testOnCreate);
            return this;
        }


        public Builder setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            return this;
        }

        public Builder setBlockWhenExhausted(boolean blockWhenExhausted) {
            config.setBlockWhenExhausted(blockWhenExhausted);
            return this;
        }

        public Builder setMinIdlePerKey(int minIdlePerKey) {
            config.setMinIdlePerKey(minIdlePerKey);
            return this;
        }

        public Builder setMaxIdlePerKey(int maxIdlePerKey) {
            config.setMaxIdlePerKey(maxIdlePerKey);
            return this;
        }

        public Builder setMaxTotalPerKey(int maxTotalPerKey) {
            config.setMaxTotalPerKey(maxTotalPerKey);
            return this;
        }

        public Builder setMaxTotal(int maxTotal) {
            config.setMaxTotal(maxTotal);
            return this;
        }

        public SftpPoolConfig build(){
            return config;
        }

    }

}
