package me.meilon.jsftp.autoconfigure;


import me.meilon.jsftp.core.conf.SftpConnConfig;

/**
 * sftp 链接配置
 * @author meilon
 */
public class JsftpConnProperties {

    /**
     * 为sftp定义一个ID
     * 可选, 如果不定义则按照默认规则生成
     * @see SftpConnConfig#getId()
     */
    private String id;

    private String host;

    private int port;

    private String userName;

    private String password;

    private Boolean autoDisconnect = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutoDisconnect() {
        return autoDisconnect;
    }

    public void setAutoDisconnect(Boolean autoDisconnect) {
        this.autoDisconnect = autoDisconnect;
    }
}
