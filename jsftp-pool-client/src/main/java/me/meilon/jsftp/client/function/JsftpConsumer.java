package me.meilon.jsftp.client.function;

import com.jcraft.jsch.SftpException;
import me.meilon.jsftp.core.SftpConnect;


/**
 * 一个没有返回对象的函数接口
 * @author meilon
 */
@FunctionalInterface
public interface JsftpConsumer {


    /**
     * 执行 sftp 操作
     * 没有返回
     * @param sftp sftp链接
     * @throws SftpException sftp操作异常
     */
    void accept(SftpConnect sftp) throws SftpException;

}
