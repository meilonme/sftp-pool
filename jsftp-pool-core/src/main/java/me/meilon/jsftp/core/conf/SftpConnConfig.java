package me.meilon.jsftp.core.conf;

/**
 * sftp 链接配置
 * @author meilon
 */
public class SftpConnConfig {


    private final String id;

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
    private final String alias;
    /**
     * sftp服务器登录后的初始远程目录
     * 可以手动设定 basePath, 连接池将会在激活链接时自动 cd 到此目录
     * 如果不设定 basePath, 则使用 sftp 链接的 homePath
     */
    private String basePath;

    /**
     * 是否自动关闭链接
     * 如果设为 true, 则每次归还 sftp 链接到连接池的时候会自动关闭链接
     */
    private boolean autoDisconnect = false;

    public SftpConnConfig(String host, int port, String userName, String password) {
        this(host,port,userName,password,null);
    }

    public SftpConnConfig(String host, int port, String userName, String password, String id) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.alias = userName + "@" + host + ":" + port;
        if (id != null && !id.isEmpty()){
            this.id = id;
        }
        else {
            this.id = this.alias;
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

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isAutoDisconnect() {
        return autoDisconnect;
    }

    public void setAutoDisconnect(boolean autoDisconnect) {
        this.autoDisconnect = autoDisconnect;
    }

    @Override
    public String toString() {
        return "SftpConnConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", homePath='" + basePath + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
