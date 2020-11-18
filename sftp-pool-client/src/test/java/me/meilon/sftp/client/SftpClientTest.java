package me.meilon.sftp.client;


import me.meilon.sftp.core.conf.SftpPoolConfig;

public class SftpClientTest {
    public static void main(String[] args) {
        SftpHelper.init(new SftpPoolConfig());
        SftpClient client = SftpHelper.createSftpClient("192.168.81.167",20022, "ideal","D9gUnZE9l^");
        client.testRun();

    }
}
