package me.meilon.jsftp.core.conf;

/**
 * sftp 文件传输模式
 * @author meilon
 */
public enum SftpMode {

    /**
     * 完全覆盖模式，这是JSch的默认文件传输模式，即如果目标文件已经存在，传输的文件将完全覆盖目标文件，产生新的文件。
     */
    OVERWRITE(0),
    /**
     * 恢复模式，如果文件已经传输一部分，这时由于网络或其他任何原因导致文件传输中断，如果下一次传输相同的文件，
     * 则会从上一次中断的地方续传。
     */
    RESUME(1),
    /**
     * 追加模式，如果目标文件已存在，传输的文件将在目标文件后追加。
     */
    APPEND(2);

    int mode;

    SftpMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
