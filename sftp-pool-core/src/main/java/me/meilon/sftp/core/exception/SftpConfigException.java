package me.meilon.sftp.core.exception;

/**
 * @author meilon
 */
public class SftpConfigException extends RuntimeException {

    public SftpConfigException() {
    }

    public SftpConfigException(String message) {
        super(message);
    }

    public SftpConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public SftpConfigException(Throwable cause) {
        super(cause);
    }

    public SftpConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
