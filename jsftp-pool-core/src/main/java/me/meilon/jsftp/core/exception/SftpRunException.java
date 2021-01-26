package me.meilon.jsftp.core.exception;

/**
 * @author meilon
 */
public class SftpRunException extends RuntimeException {

    public SftpRunException() {
    }

    public SftpRunException(String message) {
        super(message);
    }

    public SftpRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public SftpRunException(Throwable cause) {
        super(cause);
    }

    public SftpRunException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
