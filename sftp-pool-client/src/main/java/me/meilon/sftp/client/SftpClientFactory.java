package me.meilon.sftp.client;

import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.SftpPooledFactory;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.exception.SftpConfigException;

/**
 * @author meilon
 */
public class SftpClientFactory {

    private static SftpPooledFactory pooledFactory;

    public static void init(SftpPooledFactory factory){
        pooledFactory = factory;
    }

    private static void checkInit(){
        if (pooledFactory == null){
            throw new SftpConfigException("factory not initialization");
        }
    }


    public static SftpClient createSftpClient(String host, Integer port,
                                              String user, String password){
        checkInit();
        SftpPool pool = pooledFactory.getSftpPool();
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password);
        return new SftpClient(pool,config);
    }

    public static SftpClient createSftpClient(String host, Integer port,
                                              String user, String password, String sftpName){
        checkInit();
        SftpPool pool = pooledFactory.getSftpPool();
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password,sftpName);
        return new SftpClient(pool,config);
    }

    public static SftpPool getSftpPool(){
        checkInit();
        return pooledFactory.getSftpPool();
    }

    public static SftpConnConfig getSftpConnConfig(String host, Integer port,
                                                   String user, String password){
        checkInit();
        return pooledFactory.setSftpConnConfig(host,port,user,password);
    }

    public static SftpConnConfig getSftpConnConfig(String host, Integer port,
                                                   String user, String password, String sftpName){
        checkInit();
        return pooledFactory.setSftpConnConfig(host,port,user,password,sftpName);
    }
}
