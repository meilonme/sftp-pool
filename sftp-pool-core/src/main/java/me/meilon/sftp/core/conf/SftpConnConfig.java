package me.meilon.sftp.core.conf;

/**
 * sftp 链接配置
 * @author meilon
 */
public class SftpConnConfig {

    private final String host;

    private final int port;

    private final String userName;

    private final String password;

    /**
     * 给 sftp链接起一个别名
     * 可选
     * 如果不填则会自动生成一个
     * 生成规则 userName + "@" + host + ":" port
     */
    private String sftpName;

    public SftpConnConfig(String host, int port, String userName, String password) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.sftpName = userName + "@" + host + ":" + port;
    }

    public SftpConnConfig(String host, int port, String userName, String password, String sftpName) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        if (sftpName != null && !sftpName.isEmpty()){
            this.sftpName = sftpName;
        }
        else {
            this.sftpName = userName + "@" + host + ":" + port;
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getSftpName() {
        return sftpName;
    }

    public void setSftpName(String sftpName) {
        this.sftpName = sftpName;
    }
}
