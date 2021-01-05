package me.meilon.sftp.client;


import me.meilon.sftp.core.Factory;
import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.SftpPooledFactory;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;

public class SftpClientTest {
    public static void main(String[] args) {
        SftpPooledFactory factory = Factory.INSTANCE.getFactory();
        SftpPoolConfig config = new SftpPoolConfig();
        SftpPool pool = factory.createSftpPool(config);
        SftpConnConfig connConfig = new SftpConnConfig("192.168.81.167",
                20022,"ideal","");
        SftpClient client = new SftpClient(connConfig, pool);
        client.pwd();
    }
}
