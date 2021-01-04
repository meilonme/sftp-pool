package me.meilon.sftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpPoolConfig;

import java.io.OutputStream;
import java.util.List;

public class SftpCoreTest {


    public static void main(String[] args) {
//        SftpPoolConfig conf = new SftpPoolConfig();
//        SftpPooledFactory factory = Factory.INSTANCE.getFactory();
//        SftpPool pool = factory.createSftpPool(conf);
//        SftpConnConfig config = factory.setSftpConnConfig("192.168.81.167",20022, "ideal","D9gUnZE9l^");
//        try (SftpConnect sftp = pool.borrowObject(config)){
        try (SftpConnect sftp = SftpPooledFactory.createConnect("192.168.81.167",20022, "ideal","D9gUnZE9l^")){
            List<ChannelSftp.LsEntry> files =  sftp.listFiles("/apps/");
            for (ChannelSftp.LsEntry file : files) {

                System.out.println(file.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
