package me.meilon.sftp.core;

import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;

public class SftpCoreTest {


    public static void main(String[] args) {
        System.out.println(1);
        SftpPoolConfig conf = new SftpPoolConfig();
        conf.setTestOnBorrow(true);
        conf.setTestWhileIdle(true);
        conf.setTestOnCreate(true);
        conf.setTestOnReturn(true);
        System.out.println(2);
        SftpPooledFactory factory = Factory.INSTANCE.getFactory();
        System.out.println(3);
        SftpPool pool = factory.createSftpPool(conf);
        SftpConnConfig config = factory.setSftpConnConfig("192.168.81.167",20022, "ideal","D9gUnZE9l^");
        for (int i = 0; i < 1; i++) {
            try (SftpConnect sftp = pool.borrowObject(config)){
                String pwd = sftp.pwd();
                System.out.println(pwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("end");
    }
}
