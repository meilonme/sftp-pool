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
public class JsftpClientFactory extends SftpPooledFactory{


    public JsftpClientFactory() {
        super();
    }

    public JsftpClientFactory(Map<String, SftpConnConfig> connConfigMap) {
        super(connConfigMap);
    }

    public JsftpClientFactory(SftpPoolConfig sftpPoolConfig) {
        super(sftpPoolConfig);
    }

    public JsftpClientFactory(Map<String, SftpConnConfig> connConfigMap, SftpPoolConfig sftpPoolConfig) {
        super(connConfigMap, sftpPoolConfig);
    }

    public JsftpClient createSftpClient(String host, Integer port,
                                        String user, String password){

        SftpConnConfig config = setSftpConnConfig(host,port,user,password);
        return new JsftpClient(getSftpPool(),config);
    }

    public JsftpClient createSftpClient(String host, Integer port,
                                        String user, String password, String sftpId){
        SftpConnConfig config = setSftpConnConfig(host,port,user,password,sftpId);
        return new JsftpClient(getSftpPool(), config);
    }

    public JsftpClient createSftpClient(String host, Integer port,
                                        String user, String password,
                                        String sftpId, boolean autoDisconnect){
        SftpConnConfig config = setSftpConnConfig(host,port,user,password,sftpId, autoDisconnect);
        return new JsftpClient(getSftpPool(), config);
    }

}
