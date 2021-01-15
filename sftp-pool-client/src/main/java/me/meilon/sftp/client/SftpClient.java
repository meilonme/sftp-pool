package me.meilon.sftp.client;

import me.meilon.sftp.client.excetpion.SftpClientRunException;
import me.meilon.sftp.core.SftpConnect;
import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.conf.SftpConnConfig;


import java.util.function.Consumer;
import java.util.function.Function;

/**
 * sftp 客户端
 * 主要实现自动获取和归还sftp链接, 避免忘记关闭sftp链接的情况
 * @author meilon
 */
public class SftpClient {

    private final SftpPool pool;
    private final SftpConnConfig config;

    public SftpClient(SftpPool pool, SftpConnConfig config) {
        this.pool = pool;
        this.config = config;
    }

    public SftpClient(String host, Integer port, String user, String password) {
        pool = SftpClientFactory.getSftpPool();
        config = SftpClientFactory.getSftpConnConfig(host,port,user,password);
    }

    public SftpClient(String host, Integer port, String user, String password, String sftpName) {
        pool = SftpClientFactory.getSftpPool();
        config = SftpClientFactory.getSftpConnConfig(host,port,user,password,sftpName);
    }

    /**
     * 提供函数式方式使用 SftpConnect
     * 有返回值
     * @param fun 执行函数
     * @param <P> 执行结果
     * @return 执行结果
     */
    public <P> P run(Function<SftpConnect, P > fun){
        try (SftpConnect sftp = pool.borrowObject(config)){
            return fun.apply(sftp);
        } catch (Exception e) {
            throw new SftpClientRunException(e);
        }
    }

    /**
     * 提供函数式方式使用 SftpConnect
     * 无返回值
     * @param fun 执行函数
     */
    public void run(Consumer<SftpConnect> fun){
        try (SftpConnect sftp = pool.borrowObject(config)){
            fun.accept(sftp);
        } catch (Exception e) {
            throw new SftpClientRunException(e);
        }
    }
}
