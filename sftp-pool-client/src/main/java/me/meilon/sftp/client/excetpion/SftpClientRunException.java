package me.meilon.sftp.client.excetpion;

public class SftpClientRunException extends RuntimeException {

    public SftpClientRunException() {
    }

    public SftpClientRunException(String message) {
        super(message);
    }

    public SftpClientRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public SftpClientRunException(Throwable cause) {
        super(cause);
    }

    public SftpClientRunException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
