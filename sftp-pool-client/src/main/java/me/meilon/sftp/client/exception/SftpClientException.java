package me.meilon.sftp.client.exception;

/**
 * @author meilon
 */
public class SftpClientException extends RuntimeException {

    public SftpClientException() {
    }

    public SftpClientException(String message) {
        super(message);
    }

    public SftpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public SftpClientException(Throwable cause) {
        super(cause);
    }

    public SftpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
