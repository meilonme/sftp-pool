package me.meilon.jsftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import me.meilon.jsftp.core.exception.SftpConfigException;
import me.meilon.jsftp.core.conf.SftpConnConfig;
import me.meilon.jsftp.core.conf.SftpPoolConfig;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;

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
     * 创建一个 sftp 链接, 此链接不会交给链接池管理
     *
     * 注: 此工厂静态方法创建的 sftp 链接不会纳管到链接池中, 注意自行关闭
     * 此方法保留给特殊情况下使用, 不推荐使用;
     * 如果sftp服务端对链接有某些限制情况需要在使用完成后立即关闭, 建议在配置中使用 autoDisconnect
     * @see SftpConnConfig#setAutoDisconnect(boolean)
     * 设置 AutoDisconnect 后, 链接池会在交还链接的时候自动执行关闭操作
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
    private static SftpConnect createConnect(String host, Integer port,
                                            String user, String password,
                                            String id) throws JSchException {
        JSch jsch = new JSch();
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig(sshConfig);
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        SftpConnConfig conf = new SftpConnConfig(host, port, user, password, id);
        return new SftpConnect(conf, channel);
    }


    /**
     * 通过sftp连接池工厂关闭 sftp链接
     * 此方法会判断sftp链接是否纳管到了连接池
     * 如果没有则直接关闭
     * 如果有则交还给连接池
     * @param sftpConnect sftp链接对象
     */
    public static void closeSftp(SftpConnect sftpConnect){
        if (sftpConnect == null){
            return;
        }
        if (!sftpConnect.isPooledObject() || pool == null || sftpConnect.getId() == null){
            sftpConnect.disconnect();
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
    public SftpPool getSftpPool(){
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
     * 生成一个 sftp 链接配置对象
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     */
    public SftpConnConfig setSftpConnConfig(String host, Integer port,
                                            String user, String password){
        SftpConnConfig conf = new SftpConnConfig(host, port, user, password);
        setSftpConnConfig(conf);
        return conf;
    }

    /**
     *
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @param sftpId sftp的唯一id
     * @return sftp链接配置对象
     */
    public SftpConnConfig setSftpConnConfig(String host, Integer port,
                                            String user, String password, String sftpId){
        SftpConnConfig conf = new SftpConnConfig(host, port, user, password, sftpId);
        setSftpConnConfig(conf);
        return conf;
    }

    /**
     *
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @param autoDisconnect 是否自动断开链接
     *                       设为true: 归还sftp链接时自动关闭链接
     * @return sftp链接配置对象
     */
    public SftpConnConfig setSftpConnConfig(String host, Integer port,
                                            String user, String password, boolean autoDisconnect){
        SftpConnConfig conf = new SftpConnConfig(host, port, user, password);
        conf.setAutoDisconnect(autoDisconnect);
        setSftpConnConfig(conf);
        return conf;
    }

    /**
     *
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @param sftpId sftp的唯一id
     * @param autoDisconnect 是否自动断开链接
     *                       设为true: 归还sftp链接时自动关闭链接
     * @return sftp链接配置对象
     */
    public SftpConnConfig setSftpConnConfig(String host, Integer port,
                                            String user, String password,
                                            String sftpId, boolean autoDisconnect){
        SftpConnConfig conf = new SftpConnConfig(host, port, user, password, sftpId);
        conf.setAutoDisconnect(autoDisconnect);
        setSftpConnConfig(conf);
        return conf;
    }


    /**
     * 添加一个 sftp 链接配置
     * @param config sftp 链接配置
     */
    public void setSftpConnConfig(SftpConnConfig config){

        if (config == null){
            throw new NullPointerException("SftpConnConfig is null!");
        }
        String key = config.getId();
        SftpConnConfig oldConf = connConfigMap.get(key);
        if (oldConf != null &&
                oldConf.getPassword().equals(config.getPassword())){
            return;
        }
        connConfigMap.put(key, config);
    }

    /**
     * 批量设置sftp链接配置
     * @param configMap sftp链接配置Map对象
     */
    public void setSftpConnConfigMap(Map<String,SftpConnConfig> configMap){
        for (Map.Entry<String, SftpConnConfig> item : configMap.entrySet()) {
            connConfigMap.put(item.getKey(),item.getValue());
        }
    }

    /**
     * 根据 id 获取 sftp链接配置
     * @param sftpId 指定sftp的唯一id
     * @return sftp链接配置对象
     */
    public SftpConnConfig getSftpConnConf(String sftpId){
        return connConfigMap.get(sftpId);
    }


    /**
     * 创建 sftpConnect 对象
     * @param sftpId 指定sftp的唯一id
     * @return sftp链接对象
     * @throws Exception 创建失败抛出异常
     */
    @Override
    public SftpConnect create(String sftpId) throws Exception {
        SftpConnConfig conf = connConfigMap.get(sftpId);
        if (conf == null){
            throw new SftpConfigException("get sftpConfig is null! ");
        }
        SftpConnect connect = createConnect(conf.getHost(), conf.getPort(), conf.getUserName(), conf.getPassword(), sftpId);
        // 如果没有设置 homePath, 则根据初始 pwd 设置
        String homePath = conf.getBasePath();
        if (homePath == null){
            homePath = connect.getHome();
            conf.setBasePath(homePath);
        }
        return connect;
    }

    /**
     * 将 sftp 链接包装为池化对象
     *
     * @param value 被包装的 sftp 链接
     * @return 对象包装器
     */
    @Override
    public PooledObject<SftpConnect> wrap(SftpConnect value) {
        value.setPooledObject(true);
        return new SftpPooledObject(value);
    }

    /**
     * 验证 sftp 链接是否有效
     * Pool中不能保存无效的"对象",因此"后台检测线程"会周期性的检测 Pool中"对象"的有效性,
     * 如果对象无效则会导致此对象从 Pool中移除,并 destroy;
     * 此外在调用者从Pool获取一个"对象"时,也会检测"对象"的有效性,确保不能将"无效"的对象输出给调用者;
     * 当调用者使用完毕将"对象"归还到 Pool时,仍然会检测对象的有效性.所谓有效性,就是此"对象"的状态是否符合预期,是否可以对调用者直接使用;
     * 如果对象是Socket,那么它的有效性就是socket的通道是否畅通/阻塞是否超时等.
     * @param sftpId 指定sftp的唯一id
     * @param p 池化对象
     * @return true 链接有效, false 链接无效
     */
    @Override
    public boolean validateObject(String sftpId, PooledObject<SftpConnect> p) {
        boolean res = false;
        if (p != null){
            SftpConnConfig config = connConfigMap.get(sftpId);
            // 如果开启了自动关闭, 则在交还时直接返回无效, 由连接池自动关闭sftp链接
            if (config.isAutoDisconnect() && PooledObjectState.RETURNING == p.getState()){
                return false;
            }
            SftpConnect bean = p.getObject();
            if (bean != null){
                res = bean.isConnected();
            }
        }
        log.debug("validateObject {} = {}", sftpId, res);
        return res;
    }

    /**
     * 销毁 sftp 链接
     * 如果对象池中检测到某个"对象"idle的时间超时,或者操作者向对象池"归还对象"时检测到"对象"已经无效,那么此时将会导致"对象销毁";
     * 当调用此方法时,"对象"的生命周期必须结束.如果object是线程,那么此时线程必须退出;
     * 如果object是socket操作,那么此时socket必须关闭;
     * 如果object是文件流操作,那么此时"数据flush"且正常关闭.
     *
     * @param sftpId 指定sftp的唯一id
     * @param p 池化对象
     * @throws Exception 销毁失败时抛出异常
     */
    @Override
    public void destroyObject(String sftpId, PooledObject<SftpConnect> p)
            throws Exception {
        if (p != null){
            SftpConnect sftp = p.getObject();
            if (sftp != null){
                sftp.disconnect();
            }
            p.markAbandoned();
            super.destroyObject(sftpId, p);
            log.debug("destroyObject {}, NumActive {}, NumIdle {}",
                    sftpId, pool.getNumActive(sftpId), pool.getNumIdle(sftpId));
        }
    }

    /**
     * 激活
     * 当Pool中决定移除一个对象交付给调用者时额外的"激活"操作,
     * 比如可以在 activateObject方法中"重置"参数列表让调用者使用时感觉像一个"新创建"的对象一样;
     * 如果object是一个线程,可以在"激活"操作中重置"线程中断标记",或者让线程从阻塞中唤醒等;
     * 如果object是一个socket,那么可以在"激活操作"中刷新通道,或者对socket进行链接重建(假如socket意外关闭)等.
     * @param sftpId 指定sftp的唯一id
     * @param p 池化对象
     * @throws Exception 激活失败时抛出异常
     */
    @Override
    public void activateObject(String sftpId, PooledObject<SftpConnect> p)
            throws Exception {
        SftpConnect conn = p.getObject();
        SftpConnConfig config = connConfigMap.get(sftpId);
        String homePath = config.getBasePath();
        if (homePath != null){
            conn.cd(homePath);
        }
        super.activateObject(sftpId, p);
    }

    /**
     * "钝化"对象,当调用者"归还对象"时,Pool将会"钝化对象"；
     * 钝化的言外之意,就是此"对象"暂且需要"休息"一下.
     * 如果object是一个socket,那么可以passivateObject中清除buffer,将socket阻塞;
     * 如果object是一个线程,可以在"钝化"操作中将线程sleep或者将线程中的某个对象wait.
     * 需要注意,activateObject和 passivateObject两个方法需要对应,避免死锁或者"对象"状态的混乱.
     * @param sftpId 指定sftp的唯一id
     * @param p 池化对象
     * @throws Exception 钝化失败时抛出异常
     */
    @Override
    public void passivateObject(String sftpId, PooledObject<SftpConnect> p)
            throws Exception {
        super.passivateObject(sftpId, p);
    }

}