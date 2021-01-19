package me.meilon.jsftp.client.function;

import com.jcraft.jsch.SftpException;
import me.meilon.jsftp.core.SftpConnect;


/**
 * @author meilon
 * @param <R> return 对象
 */
@FunctionalInterface
public interface JsftpFunction<R>  {

    /**
     * 执行 sftp 操作
     * @param sftp sftp链接
     * @return 执行结果
     * @throws SftpException sftp执行异常
     */
    R apply(SftpConnect sftp) throws SftpException;

}
