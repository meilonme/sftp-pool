package me.meilon.jsftp.client;

import me.meilon.jsftp.core.SftpPooledFactory;
import me.meilon.jsftp.core.conf.SftpConnConfig;
import me.meilon.jsftp.core.conf.SftpPoolConfig;

import java.util.Map;

/**
 * @author meilon
 */
public class JsftpClientFactory extends SftpPooledFactory{


    private JsftpClientFactory() {
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
