package me.meilon.sftp.core.exception;

/**
 * @author meilon
 */
public class SftpConnectException extends RuntimeException {

    public SftpConnectException() {
    }

    public SftpConnectException(String message) {
        super(message);
    }

    public SftpConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SftpConnectException(Throwable cause) {
        super(cause);
    }

    public SftpConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
