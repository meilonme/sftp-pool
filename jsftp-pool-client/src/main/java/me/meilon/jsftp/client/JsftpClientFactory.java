package me.meilon.jsftp.client;

import me.meilon.jsftp.core.SftpPooledFactory;
import me.meilon.jsftp.core.conf.SftpConnConfig;
import me.meilon.jsftp.core.conf.SftpPoolConfig;

import java.util.Map;

/**
 * JsftpClient 工厂类
 * 用于构造 JsftpClient
 * @author meilon
 */
public class JsftpClientFactory{

    private final SftpPooledFactory pooledFactory;

    public JsftpClientFactory() {
        this.pooledFactory = new SftpPooledFactory();
    }

    public JsftpClientFactory(Map<String, SftpConnConfig> connConfigMap) {
        this.pooledFactory = new SftpPooledFactory(connConfigMap);
    }

    public JsftpClientFactory(SftpPoolConfig sftpPoolConfig) {
        this.pooledFactory = new SftpPooledFactory(sftpPoolConfig);
    }

    public JsftpClientFactory(Map<String, SftpConnConfig> connConfigMap, SftpPoolConfig sftpPoolConfig) {
        this.pooledFactory = new SftpPooledFactory(connConfigMap,sftpPoolConfig);
    }

    public JsftpClient createSftpClient(String host, Integer port,
                                        String user, String password){

        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password);
        return new JsftpClient(pooledFactory.getSftpPool(),config);
    }

    public JsftpClient createSftpClient(String host, Integer port,
                                        String user, String password, String sftpId){
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password,sftpId);
        return new JsftpClient(pooledFactory.getSftpPool(), config);
    }

    public JsftpClient createSftpClient(String host, Integer port,
                                        String user, String password,
                                        String sftpId, boolean autoDisconnect){
        SftpConnConfig config = pooledFactory.setSftpConnConfig(host,port,user,password,sftpId, autoDisconnect);
        return new JsftpClient(pooledFactory.getSftpPool(), config);
    }

    public SftpPooledFactory getPooledFactory() {
        return pooledFactory;
    }
}
