package me.meilon.sftp.core;

/**
 * @author meilon
 *
 */
public enum Factory {

    // 枚举方式实现sftp连接池工厂的单例模式
    INSTANCE;

    private final SftpPooledFactory factory;

    Factory() {
        this.factory = new SftpPooledFactory();
    }

    public SftpPooledFactory getFactory(){
        return factory;
    }

}
