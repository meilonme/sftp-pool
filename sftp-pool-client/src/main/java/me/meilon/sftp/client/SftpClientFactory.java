package me.meilon.sftp.client;

import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.SftpPooledFactory;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.exception.SftpConfigException;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author meilon
 */
public class SftpClientFactory {

    private static ConfigurableApplicationContext applicationContext;
    private static SftpPooledFactory pooledFactory;
    private static SftpPool pool;

    public static void init(){
        pooledFactory = applicationContext.getBean(SftpPooledFactory.class);
        pool = pooledFactory.getSftpPool();
    }
    /**
     * 初始化方法
     *
     * @param factory sftp连接池工厂
     */
    public static void init(SftpPooledFactory factory){
        pooledFactory = factory;
        pool = pooledFactory.getSftpPool();
    }

    private static void checkInit(){
        if (pooledFactory == null){
            init();
        }
    }


    public static SftpClient createSftpClient(String host, Integer port,
                                              String user, String password){
        checkInit();
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password);
        return new SftpClient(pool,config);
    }

    public static SftpClient createSftpClient(String host, Integer port,
                                              String user, String password, String sftpId){
        checkInit();
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password,sftpId);
        return new SftpClient(pool,config);
    }

    public static SftpPool getSftpPool(){
        checkInit();
        return pool;
    }

    public static SftpConnConfig getSftpConnConfig(String host, Integer port,
                                                   String user, String password){
        checkInit();
        return pooledFactory.setSftpConnConfig(host,port,user,password);
    }

    public static SftpConnConfig getSftpConnConfig(String host, Integer port,
                                                   String user, String password, String sftpId){
        checkInit();
        return pooledFactory.setSftpConnConfig(host,port,user,password,sftpId);
    }

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        SftpClientFactory.applicationContext = applicationContext;
    }
}
