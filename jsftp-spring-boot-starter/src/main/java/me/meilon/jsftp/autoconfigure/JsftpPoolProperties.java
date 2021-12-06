package me.meilon.jsftp.autoconfigure;


import me.meilon.jsftp.core.conf.SftpConnConfig;
import me.meilon.jsftp.core.conf.SftpPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * sftp 连接池 spring 配置类
 * @author meilon
 */
@ConfigurationProperties(prefix = "sftp-pool")
public class JsftpPoolProperties extends SftpPoolConfig {

    /**
     * sftp 链接配置
     */
    private Map<String, JsftpConnProperties> connConfigs;

    public Map<String, JsftpConnProperties> getConnConfigs() {
        return connConfigs;
    }

    public void setConnConfigs(Map<String, JsftpConnProperties> connConfigs) {
        this.connConfigs = connConfigs;
    }

    public Map<String, SftpConnConfig> getSftpConnConfigMap(){
        Map<String, SftpConnConfig> configMap;
        if (connConfigs == null){
            configMap = new HashMap<>(16);
        }
        else{
            configMap = new HashMap<>(connConfigs.size());
            for (Map.Entry<String, JsftpConnProperties> entry : connConfigs.entrySet()) {
                JsftpConnProperties properties = entry.getValue();
                if (properties.getId() != null){
                    properties.setId(entry.getKey());
                }
                SftpConnConfig conf = new SftpConnConfig(properties.getHost(),
                        properties.getPort(),
                        properties.getUserName(),
                        properties.getPassword(),
                        properties.getId());
                conf.setAutoDisconnect(properties.getAutoDisconnect());
                configMap.put(entry.getKey(),conf);
            }
        }
        return configMap;
    }
}
