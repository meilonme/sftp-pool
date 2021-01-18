package me.meilon.jsftp.core;



import me.meilon.jsftp.core.exception.SftpConfigException;
import me.meilon.jsftp.core.conf.SftpConnConfig;
import me.meilon.jsftp.core.conf.SftpPoolConfig;
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
        String key = config.getId();
        SftpConnConfig defConfig = factory.getSftpConnConf(key);
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
        return borrowObject(conf.getId());
    }

    /**
     * 从连接池中获取一个sftp链接
     * 通过事先定义的 sftpId 获取sftp链接
     * 如果不存在则抛出异常
     * 注意: 使用完后需要自行调用 {@link SftpPool#returnSftp(SftpConnect) } 方法交还
     * @param sftpId 通过事先定义的 sftpId 获取sftp链接
     * @return sftp链接对象
     */
    @Override
    public SftpConnect borrowObject(String sftpId) throws Exception {
        SftpPooledFactory factory = (SftpPooledFactory)getFactory();
        SftpConnConfig defConfig = factory.getSftpConnConf(sftpId);
        if (defConfig == null){
            // 如果配置数据不存在或密码有变动则抛出异常
            throw new SftpConfigException("SftpConnConfig is null");
        }
        return super.borrowObject(sftpId);
    }

    /**
     * 使池中的对象失效，当获取到的对象被确定无效时（由于异常或其他问题），应该调用该方法
     * 当要把一个借走的对象置为无效的时候。（可能是因为对象的调用发生了异常或者其他未知原因）
     * @param sftpId 指定sftp的唯一id
     * @param sftpConnect sftp链接对象
     * @throws Exception 失效失败抛出异常
     */
    @Override
    public void  invalidateObject(String sftpId, SftpConnect sftpConnect) throws Exception {
        if (sftpConnect != null && sftpConnect.isConnected()){
            sftpConnect.disconnect();
        }
        super.invalidateObject(sftpId,sftpConnect);
    }

    public void invalidateSftp(SftpConnect sftpConnect) throws Exception {
        if (sftpConnect != null && sftpConnect.isConnected()){
            sftpConnect.disconnect();
            super.invalidateObject(sftpConnect.getId(),sftpConnect);
        }
    }

    /**
     * 将使用完的sftp链接交还给连接池
     * @param sftpConnect sftp链接对象
     */
    public void returnSftp(SftpConnect sftpConnect){
        returnObject(sftpConnect.getId(), sftpConnect);
    }

    /**
     * 获取活跃数, 也就是被从连接池中出来的sftp链接
     * @return 活跃状态的 sftp链接数
     */
    @Override
    public int getNumActive(){
        return super.getNumActive();
    }

    /**
     * 获取指定 Id 的活跃数
     * @param sftpId 指定sftp的唯一id
     * @return 活跃状态的 sftp链接数
     */
    @Override
    public int getNumActive(final String sftpId){
        return super.getNumActive(sftpId);
    }

    /**
     * 获取空闲数
     * @return 空闲状态的sftp链接数
     */
    @Override
    public int getNumIdle(){
        return super.getNumIdle();
    }

    /**
     * 获取指定 Id 的空闲数
     * @param sftpId 指定sftp的唯一id
     * @return 空闲状态的sftp链接数
     */
    @Override
    public int getNumIdle(final String sftpId){
        return super.getNumIdle(sftpId);
    }

    /**
     * 清除池中闲置的对象
     */
    @Override
    public void clear(){
        super.clear();
    }

    /**
     * 清除指定 Id 的对象
     * @param sftpId 指定sftp的唯一id
     */
    @Override
    public void clear(String sftpId){
        super.clear(sftpId);
    }

    /**
     * 关闭连接池
     */
    @Override
    public void close(){
        super.close();
    }
    
}
