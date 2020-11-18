package me.meilon.sftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;
import me.meilon.sftp.core.exception.SftpConnectException;
import me.meilon.sftp.core.exception.SftpConfigException;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sftp 连接池工厂
 * @author meilon
 */
public class SftpPooledFactory extends BaseKeyedPooledObjectFactory<String, SftpConnect> {

    /**
     * 记录sftp链接的配置数据
     */
    private final Map<String, SftpConnConfig> connConfigMap;
    private SftpPoolConfig sftpPoolConfig;
    private volatile static SftpPool pool;

    private static final Properties SSH_CONFIG;
    static {
        SSH_CONFIG = new Properties();
        SSH_CONFIG.put("StrictHostKeyChecking", "no");
    }

    protected SftpPooledFactory(){
        connConfigMap = new ConcurrentHashMap<>(16);
    }


    /**
     * 创建一个 sftp 链接
     *
     * 注: 此方法创建的 sftp 链接不会纳管到链接池中, 注意自行关闭
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @return sftp 链接
     */
    public static SftpConnect createConnect(String host, Integer port, String user, String password){
        return createConnect(host,port,user,password, null);
    }
    public static SftpConnect createConnect(String host, Integer port, String user, String password, String sftpName){
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig(SSH_CONFIG);
            session.connect();
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            return new SftpConnect(sftpName, channel);
        } catch (JSchException e) {
            throw new SftpConnectException(e);
        }

    }


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
     * 单例模式创建sftp链接池
     * @param sftpPoolConfig sftp链接池配置数据
     * @return sftp链接池
     */
    public SftpPool createSftpPool(SftpPoolConfig sftpPoolConfig){
        if (pool == null){
            synchronized (SftpPool.class){
                if (pool == null){
                    this.sftpPoolConfig = sftpPoolConfig;
                    pool = new SftpPool(this, sftpPoolConfig);
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


    @Override
    public PooledObject<SftpConnect> wrap(SftpConnect value) {
        System.out.println("wrap");
        value.setPool(true);
        return new DefaultPooledObject<>(value);
    }

    /**
     * 验证对象是否有效
     * @param key
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(String key, PooledObject<SftpConnect> p) {
        System.out.println("validateObject");
        SftpConnect bean = p.getObject();
        return bean.isConnected();
    }

    /**
     * 销毁
     * @param key
     * @param p
     * @throws Exception
     */
    @Override
    public void destroyObject(String key, PooledObject<SftpConnect> p)
            throws Exception {
        System.out.println("destroyObject");
        p.getObject().close();
        super.destroyObject(key, p);
    }

    /**
     * 激活
     * @param key
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(String key, PooledObject<SftpConnect> p)
            throws Exception {
        System.out.println("activateObject");
        super.activateObject(key, p);
    }

    @Override
    public void passivateObject(String key, PooledObject<SftpConnect> p)
            throws Exception {
        System.out.println("passivateObject");
        super.passivateObject(key, p);
    }

}