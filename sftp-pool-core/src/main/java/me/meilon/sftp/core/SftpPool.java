package me.meilon.sftp.core;



import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;


/**
 * sftp 链接池
 * @author meilon
 */
public class SftpPool {

    private static GenericKeyedObjectPool<String, SftpConnect> pool;


    protected SftpPool(SftpPooledFactory factory, SftpPoolConfig config) {
        pool = new GenericKeyedObjectPool<>(factory,config);
    }

    /**
     * 从连接池中获取一个sftp链接
     * 注意: 使用完后需要自行调用 {@link #returnSftp(SftpConnect) } 方法交还
     * @param config sftp链接配置
     * @return sftp链接对象
     */
    public SftpConnect borrowObject(SftpConnConfig config) throws Exception {
        if (config == null){
            throw new NullPointerException("SftpConnConfig is null");
        }
        SftpPooledFactory factory = (SftpPooledFactory)pool.getFactory();
        String key = config.getSftpName();
        SftpConnConfig defConfig = factory.getConf(key);
        if (defConfig == null ||
                !defConfig.getPassword().equals(config.getPassword())){
            // 如果配置数据不存在或密码有变动则更新
            factory.setSftpConnConfig(config);
        }
        return pool.borrowObject(key);
    }

    /**
     * 将使用完的sftp链接交还给连接池
     * @param sftpConnect sftp链接对象
     */
    public void returnSftp(SftpConnect sftpConnect){
        pool.returnObject(sftpConnect.getFtpName(), sftpConnect);
    }

    public void setConfig(SftpPoolConfig config){
        pool.setConfig(config);
    }
    
}
