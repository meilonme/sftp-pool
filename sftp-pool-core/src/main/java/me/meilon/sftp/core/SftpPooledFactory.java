package me.meilon.sftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;
import me.meilon.sftp.core.exception.SftpConfigException;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sftp 连接池工厂
 *
 * 池化对象的状态定义在 {@link org.apache.commons.pool2.PooledObjectState} 枚举中，有以下值
 * IDLE 在池中，处于空闲状态
 * ALLOCATED 被使用中
 * EVICTION 正在被逐出器验证
 * VALIDATION 正在验证
 * INVALID 驱逐测试或验证失败并将被销毁
 * ABANDONED 对象被客户端拿出后，长时间未返回池中，或没有调用 use 方法，即被标记为抛弃的
 * @author meilon
 */
@Slf4j
public class SftpPooledFactory extends BaseKeyedPooledObjectFactory<String, SftpConnect> {

    /**
     * sftp链接的配置数据
     */
    private final Map<String, SftpConnConfig> connConfigMap;
    /**
     * sftp 链接池配置
     */
    private SftpPoolConfig sftpPoolConfig;
    /**
     * sftp 连接池
     */
    private volatile static SftpPool pool;


    public SftpPooledFactory(int size){
        connConfigMap = new ConcurrentHashMap<>(size);
    }

    public SftpPooledFactory(){
        this(16);
    }

    public SftpPooledFactory(SftpPoolConfig sftpPoolConfig) {
        this();
        this.sftpPoolConfig = sftpPoolConfig;
    }

    public SftpPooledFactory(Map<String, SftpConnConfig> connConfigMap){
        this(connConfigMap.size());
        setSftpConnConfigMap(connConfigMap);
    }

    public SftpPooledFactory(Map<String, SftpConnConfig> connConfigMap, SftpPoolConfig sftpPoolConfig){
        this(connConfigMap);
        this.sftpPoolConfig = sftpPoolConfig;
    }


    /**
     * 创建一个 sftp 链接
     *
     * 注: 此工厂静态方法创建的 sftp 链接不会纳管到链接池中, 注意自行关闭
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @return sftp 链接
     */
    public static SftpConnect createConnect(String host, Integer port,
                                            String user, String password) throws JSchException {
        return createConnect(host,port,user,password, null);
    }
    public static SftpConnect createConnect(String host, Integer port,
                                            String user, String password,
                                            String sftpName) throws JSchException {
        JSch jsch = new JSch();
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig(sshConfig);
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return new SftpConnect(sftpName, channel);
    }


    /**
     * 通过sftp连接池工厂关闭 sftp链接
     * 此方法会判断sftp链接是否纳管到了连接池
     * 如果没有则直接关闭
     * 如果有则交还给连接池
     * @param sftpConnect
     */
    public static void closeSftp(SftpConnect sftpConnect){
        if (sftpConnect == null){
            return;
        }
        if (!sftpConnect.isPool() || pool == null || sftpConnect.getFtpName() == null){
            sftpConnect.exit();
            return;
        }
        pool.returnSftp(sftpConnect);

    }

    /**
     * 单例模式创建 sftp 链接池
     * 如果在创建 工厂的时候已经设置了连接池配置则使用设置的连接池配置
     * 如果没有设置则使用默认值
     * @return sftp链接池
     */
    public SftpPool createSftpPool(){
        if (pool == null){
            synchronized (SftpPool.class){
                if (pool == null){
                    if (this.sftpPoolConfig == null){
                        this.sftpPoolConfig = new SftpPoolConfig();
                    }
                    pool = new SftpPool(this, this.sftpPoolConfig.toGenericKeyedObjectPoolConfig());
                }
            }
        }
        return pool;
    }

    /**
     * 单例模式创建sftp链接池
     * @param sftpPoolConfig sftp链接池配置数据
     * @return sftp链接池
     */
    public SftpPool createSftpPool(SftpPoolConfig sftpPoolConfig){
        if (pool == null){
            synchronized (SftpPool.class){
                if (pool == null){
                    this.sftpPoolConfig = sftpPoolConfig;
                    pool = new SftpPool(this, this.sftpPoolConfig.toGenericKeyedObjectPoolConfig());
                }
            }
        }
        return pool;
    }

    /**
     * 生成一个 sftp 链接配置对象
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     */
    public SftpConnConfig setSftpConnConfig(String host, Integer port, String user, String password){
        SftpConnConfig conf = new SftpConnConfig(host, port, user, password);
        setSftpConnConfig(conf);
        return conf;
    }

    public void setSftpConnConfigMap(Map<String,SftpConnConfig> configMap){
        for (Map.Entry<String, SftpConnConfig> item : configMap.entrySet()) {
            connConfigMap.put(item.getKey(),item.getValue());
        }
    }

    /**
     * 添加一个 sftp 链接配置
     * @param config sftp 链接配置
     */
    public void setSftpConnConfig(SftpConnConfig config){

        if (config == null){
            throw new NullPointerException("SftpConnConfig is null!");
        }
        String key = config.getSftpName();
        SftpConnConfig oldConf = connConfigMap.get(key);
        if (oldConf != null &&
                oldConf.getPassword().equals(config.getPassword())){
            return;
        }
        connConfigMap.put(key, config);
    }

    public SftpConnConfig getConf(String key){
        return connConfigMap.get(key);
    }


    /**
     * 创建 sftpConnect 对象
     * @param key
     * @return
     * @throws Exception
     */
    @Override
    public SftpConnect create(String key) throws Exception {
        SftpConnConfig conf = connConfigMap.get(key);
        if (conf == null){
            throw new SftpConfigException("get sftpConfig is null! ");
        }
        SftpConnect conn = createConnect(conf.getHost(), conf.getPort(), conf.getUserName(), conf.getPassword());
        conn.setFtpName(key);
        return conn;
    }

    /**
     * 使用 {@link PooledObject} 包装 sftp 链接
     *
     * @param value 被包装sftp链接
     * @return 对象包装器
     */
    @Override
    public PooledObject<SftpConnect> wrap(SftpConnect value) {
        value.setPool(true);
        return new DefaultPooledObject<>(value);
    }

    /**
     * 验证 sftp 链接是否有效
     * Pool中不能保存无效的"对象",因此"后台检测线程"会周期性的检测 Pool中"对象"的有效性,
     * 如果对象无效则会导致此对象从 Pool中移除,并 destroy;
     * 此外在调用者从Pool获取一个"对象"时,也会检测"对象"的有效性,确保不能将"无效"的对象输出给调用者;
     * 当调用者使用完毕将"对象"归还到 Pool时,仍然会检测对象的有效性.所谓有效性,就是此"对象"的状态是否符合预期,是否可以对调用者直接使用;
     * 如果对象是Socket,那么它的有效性就是socket的通道是否畅通/阻塞是否超时等.
     * @param key
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(String key, PooledObject<SftpConnect> p) {
        boolean res = false;
        if (p != null){
            SftpConnect bean = p.getObject();
            if (bean != null){
                res = bean.isConnected();
            }
            log.debug("IdleTimeMillis = {}", p.getIdleTimeMillis());
        }
        log.debug("validateObject {} = {}", key, res);

        return res;
    }

    /**
     * 销毁 sftp 链接
     * 如果对象池中检测到某个"对象"idle的时间超时,或者操作者向对象池"归还对象"时检测到"对象"已经无效,那么此时将会导致"对象销毁";
     * 当调用此方法时,"对象"的生命周期必须结束.如果object是线程,那么此时线程必须退出;
     * 如果object是socket操作,那么此时socket必须关闭;
     * 如果object是文件流操作,那么此时"数据flush"且正常关闭.
     *
     * @param key
     * @param p
     * @throws Exception
     */
    @Override
    public void destroyObject(String key, PooledObject<SftpConnect> p)
            throws Exception {
        if (p != null){
            SftpConnect sftp = p.getObject();
            if (sftp != null){
                sftp.exit();
            }
            p.markAbandoned();
            super.destroyObject(key, p);
            log.debug("destroyObject {}, NumActive {}, NumIdle {}",
                    key, pool.getNumActive(key), pool.getNumIdle(key));
        }
    }

    /**
     * 激活
     * 当Pool中决定移除一个对象交付给调用者时额外的"激活"操作,
     * 比如可以在 activateObject方法中"重置"参数列表让调用者使用时感觉像一个"新创建"的对象一样;
     * 如果object是一个线程,可以在"激活"操作中重置"线程中断标记",或者让线程从阻塞中唤醒等;
     * 如果object是一个socket,那么可以在"激活操作"中刷新通道,或者对socket进行链接重建(假如socket意外关闭)等.
     * @param key
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(String key, PooledObject<SftpConnect> p)
            throws Exception {
        super.activateObject(key, p);
    }

    /**
     * "钝化"对象,当调用者"归还对象"时,Pool将会"钝化对象"；
     * 钝化的言外之意,就是此"对象"暂且需要"休息"一下.
     * 如果object是一个socket,那么可以passivateObject中清除buffer,将socket阻塞;
     * 如果object是一个线程,可以在"钝化"操作中将线程sleep或者将线程中的某个对象wait.
     * 需要注意,activateObject和 passivateObject两个方法需要对应,避免死锁或者"对象"状态的混乱.
     * @param key
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(String key, PooledObject<SftpConnect> p)
            throws Exception {
        super.passivateObject(key, p);
    }

}