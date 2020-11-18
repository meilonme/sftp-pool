package me.meilon.sftp.client;


import com.jcraft.jsch.ChannelSftp;
import me.meilon.sftp.core.SftpConnect;
import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.conf.SftpConnConfig;

/**
 * sftp 客户端
 * 使用 客户端操作时会自动从连接池获取sftp链接
 * 操作完成后再重新返还给连接池
 * @author meilon
 */
public class SftpClient extends ChannelSftp implements ISftpClient {

    private final SftpPool pool;
    private final SftpConnConfig conf;


    
    public SftpClient(final SftpConnConfig conf, SftpPool pool) {
        this.pool = pool;
        this.conf = conf;
    }
    

    public void testRun(){
        System.out.println("..........");
        try(SftpConnect bean = pool.borrowObject(conf)){
            if (!bean.isConnected()){
                System.out.println("sftp未链接");
            }
            else{
                String pwd = bean.pwd();
                System.out.println(pwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
