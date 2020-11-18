package me.meilon.sftp.client;


import me.meilon.sftp.core.Factory;
import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.SftpPooledFactory;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;

/**
 *
 * @author meilon
 */
public class SftpHelper {

    private static SftpPooledFactory pooledFactory;
    private static SftpPool pool;
    private volatile static boolean isInit = false;

    public synchronized static void init(SftpPoolConfig sftpPoolConfig){
        if (!isInit){
            pooledFactory = Factory.INSTANCE.getFactory();
            pool = pooledFactory.createSftpPool(sftpPoolConfig);
            pooledFactory.setSftpConnConfigMap(sftpPoolConfig.getSftpConnConfigs());
            isInit = true;
        }
    }

    public static SftpPooledFactory createSftpPooledFactory(){
        if (!isInit){
            init(new SftpPoolConfig());
        }
        return pooledFactory;
    }

    public static SftpPool createSftpPool(){
        if (!isInit){
            init(new SftpPoolConfig());
        }
        return pool;
    }


    /**
     * 通过sftp链接配置对象创建一个Sftp客户端
     * @param config sftp链接配置对象
     * @return sftp客户端
     */
    public static SftpClient createSftpClient(SftpConnConfig config){
        return new SftpClient(config, pool);
    }

    /**
     * 通过sftp链接配置参数创建一个Sftp客户端
     * @param host sftp服务器地址
     * @param port sftp服务器端口
     * @param user sftp链接用户
     * @param password sftp链接密码
     * @return sftp客户端
     */
    public static SftpClient createSftpClient(String host, Integer port, String user, String password){
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password);
        return new SftpClient(config, pool);
    }

}
