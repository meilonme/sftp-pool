package me.meilon.sftp.autoconfigure;



import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * sftp 连接池 spring 配置类
 * @author meilon
 */
@Component
@ConfigurationProperties(prefix = "sftp-pool")
public class SftpPoolProperties extends SftpPoolConfig {


    /**
     * 使用 key 作为 sftp id
     * 如果自定义了 sftp id 则优先使用自定义的 sftp id
     */
    private boolean enableSftpName;
    /**
     * sftp 链接配置
     */
    private Map<String, SftpConnProperties> connConfigs;

    public boolean isEnableSftpName() {
        return enableSftpName;
    }

    public void setEnableSftpName(boolean enableSftpName) {
        this.enableSftpName = enableSftpName;
    }

    public Map<String, SftpConnProperties> getConnConfigs() {
        return connConfigs;
    }

    public void setConnConfigs(Map<String, SftpConnProperties> connConfigs) {
        this.connConfigs = connConfigs;
    }

    public Map<String, SftpConnConfig> getSftpConnConfigMap(){
        Map<String, SftpConnConfig> configMap = new HashMap<>(connConfigs.size());
        for (Map.Entry<String, SftpConnProperties> entry : connConfigs.entrySet()) {
            SftpConnProperties properties = entry.getValue();
            if (enableSftpName && properties.getId() != null){
                properties.setId(entry.getKey());
            }
            SftpConnConfig conf = new SftpConnConfig(properties.getHost(),
                    properties.getPort(),
                    properties.getUserName(),
                    properties.getPassword(),
                    properties.getId());
            configMap.put(entry.getKey(),conf);
        }
        return configMap;
    }
}
