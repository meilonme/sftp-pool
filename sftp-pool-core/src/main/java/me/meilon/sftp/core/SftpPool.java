package me.meilon.sftp.core;



import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;
import me.meilon.sftp.core.exception.SftpConfigException;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;


/**
 * sftp 链接池
 * @author meilon
 */
public class SftpPool extends GenericKeyedObjectPool<String, SftpConnect> {


    protected SftpPool(SftpPooledFactory factory, GenericKeyedObjectPoolConfig<SftpConnect> config) {
        super(factory,config);
    }

    /**
     * 从连接池中获取一个sftp链接
     * 注意: 使用完后需要自行调用 {@link SftpPool#returnSftp(SftpConnect) } 方法交还
     * @param config sftp链接配置
     * @return sftp链接对象
     */
    public SftpConnect borrowObject(SftpConnConfig config) throws Exception {
        if (config == null){
            throw new NullPointerException("SftpConnConfig is null");
        }
        SftpPooledFactory factory = (SftpPooledFactory)getFactory();
        String key = config.getSftpName();
        SftpConnConfig defConfig = factory.getConf(key);
        if (defConfig == null ||
                !defConfig.getPassword().equals(config.getPassword())){
            // 如果配置数据不存在或密码有变动则更新
            factory.setSftpConnConfig(config);
        }
        return borrowObject(key);
    }

    /**
     * 从连接池中获取一个sftp链接
     * 如果不存在则创建一个新连接
     *
     * @param host sftp服务ip
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @return sftp链接对象
     */
    public SftpConnect borrowObject(String host, Integer port,
                                    String user, String password) throws Exception {
        SftpPooledFactory factory = (SftpPooledFactory)getFactory();
        SftpConnConfig conf = new SftpConnConfig(host, port, user, password);
        factory.setSftpConnConfig(conf);
        return borrowObject(conf.getSftpName());
    }

    /**
     * 从连接池中获取一个sftp链接
     * 通过事先定义的 sftpName 获取sftp链接
     * 如果不存在则抛出异常
     * 注意: 使用完后需要自行调用 {@link SftpPool#returnSftp(SftpConnect) } 方法交还
     * @param sftpName 通过事先定义的 sftpName 获取sftp链接
     * @return sftp链接对象
     */
    @Override
    public SftpConnect borrowObject(String sftpName) throws Exception {
        SftpPooledFactory factory = (SftpPooledFactory)getFactory();
        SftpConnConfig defConfig = factory.getConf(sftpName);
        if (defConfig == null){
            // 如果配置数据不存在或密码有变动则抛出异常
            throw new SftpConfigException("SftpConnConfig is null");
        }
        return super.borrowObject(sftpName);
    }

    /**
     * 使池中的对象失效，当获取到的对象被确定无效时（由于异常或其他问题），应该调用该方法
     * @param key
     * @param sftpConnect
     * @throws Exception
     */
    @Override
    public void  invalidateObject(String key, SftpConnect sftpConnect) throws Exception {
        super.invalidateObject(key,sftpConnect);
    }
    /**
     * 将使用完的sftp链接交还给连接池
     * @param sftpConnect sftp链接对象
     */
    public void returnSftp(SftpConnect sftpConnect){
        returnObject(sftpConnect.getFtpName(), sftpConnect);
    }

    /**
     * 获取活跃数
     * @return
     */
    @Override
    public int getNumActive(){
        return super.getNumActive();
    }

    /**
     * 获取指定 key 的活跃数
     * @return
     */
    @Override
    public int getNumActive(final String key){
        return super.getNumActive(key);
    }

    /**
     * 获取空闲数
     * @return
     */
    @Override
    public int getNumIdle(){
        return super.getNumIdle();
    }

    /**
     * 获取指定 key 的空闲数
     * @param key
     * @return
     */
    @Override
    public int getNumIdle(final String key){
        return super.getNumIdle(key);
    }

    /**
     * 清除池中闲置的对象
     */
    @Override
    public void clear(){
        super.clear();
    }

    /**
     * 清除指定 key 的对象
     * @param key
     */
    @Override
    public void clear(String key){
        super.clear(key);
    }

    /**
     * 关闭连接池
     */
    @Override
    public void close(){
        super.close();
    }


    public void setConfig(SftpPoolConfig config){
        super.setConfig(config);
    }
    
}
