package me.meilon.jsftp.client;

import me.meilon.jsftp.client.excetpion.SftpClientRunException;
import me.meilon.jsftp.client.function.JsftpConsumer;
import me.meilon.jsftp.client.function.JsftpFunction;
import me.meilon.jsftp.core.SftpConnect;
import me.meilon.jsftp.core.SftpPool;
import me.meilon.jsftp.core.conf.SftpConnConfig;


/**
 * sftp 客户端
 * 主要实现自动获取和归还sftp链接, 避免忘记关闭sftp链接的情况
 * @author meilon
 */
public class JsftpClient {

    protected final SftpPool pool;
    protected final SftpConnConfig config;


    public JsftpClient(SftpPool pool, SftpConnConfig config) {
        this.pool = pool;
        this.config = config;
    }

    /**
     * 提供函数式方式使用 SftpConnect
     * 有返回值
     * @param function 执行函数
     * @param <P> 执行结果
     * @return 执行结果
     */
    public <P> P run(JsftpFunction<P> function){
        try (SftpConnect sftp = pool.borrowObject(config)){
            return function.apply(sftp);
        } catch (Exception e) {
            throw new SftpClientRunException(e);
        }
    }

    /**
     * 提供函数式方式使用 SftpConnect
     * 无返回值
     * @param fun 执行函数
     */
    public void run(JsftpConsumer fun){
        try (SftpConnect sftp = pool.borrowObject(config)){
            fun.accept(sftp);
        } catch (Exception e) {
            throw new SftpClientRunException(e);
        }
    }
}
