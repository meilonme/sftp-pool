package me.meilon.sftp.core.conf;

import me.meilon.sftp.core.SftpConnect;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author meilon
 */
public class SftpPoolConfig extends GenericKeyedObjectPoolConfig<SftpConnect> {

    /**
     * 最小保持的空闲链接数
     */
    private int minIdlePerKey = 0;

    /**
     * 最大保持的空闲链接数
     */
    private int maxIdlePerKey = 8;

    /**
     * 最大可存在的链接数
      */
    private int maxTotalPerKey = 8;

    /**
     * sftp 链接配置
     */
    private Map<String, SftpConnConfig> sftpConnConfigs = new HashMap<>();


    @Override
    public int getMinIdlePerKey() {
        return minIdlePerKey;
    }

    @Override
    public void setMinIdlePerKey(int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    @Override
    public int getMaxIdlePerKey() {
        return maxIdlePerKey;
    }

    @Override
    public void setMaxIdlePerKey(int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    @Override
    public int getMaxTotalPerKey() {
        return maxTotalPerKey;
    }

    @Override
    public void setMaxTotalPerKey(int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    public Map<String, SftpConnConfig> getSftpConnConfigs() {
        return sftpConnConfigs;
    }

    public void setSftpConnConfigs(Map<String, SftpConnConfig> sftpConnConfigs) {
        this.sftpConnConfigs = sftpConnConfigs;
    }
}
