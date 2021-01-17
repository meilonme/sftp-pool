package me.meilon.sftp.core;


import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import me.meilon.sftp.core.conf.SftpConnConfig;
import me.meilon.sftp.core.conf.SftpMode;
import me.meilon.sftp.core.utils.FileUtil;

import java.io.*;
import java.util.*;


/**
 * sftp 连接对象
 *
 * @author meilon
 */
@Slf4j
public class SftpConnect implements Closeable {


    private final ChannelSftp sftp;

    private final SftpConnConfig config;

    /**
     * 用于标识此链接是否通过链接池创建
     */
    private boolean isPool = false;

    protected SftpConnect(SftpConnConfig config, ChannelSftp sftp) {
        this.config = config;
        this.sftp = sftp;
    }

    public boolean isPool() {
        return isPool;
    }

    protected void setPool(boolean pool) {
        isPool = pool;
    }

    public String getId(){
        return config.getId();
    }


    public ChannelSftp getChannelSftp(){
        return sftp;
    }


    /**
     * 检查连接状态
     *
     * @return true: 连接正常  false: 连接断开
     */
    public boolean isConnected() {
        return sftp.isConnected();
    }

    /**
     * 判断目标是否是目录
     * @param directory 目录路径
     * @return true: 是目录, false: 不是目录
     * @throws SftpException SftpException
     */
    public  boolean isDir(String directory) throws SftpException {
        SftpATTRS attrs = getAttrs(directory);
        return attrs.isDir();
    }

    /**
     * 判断远程目标是否是文件
     * @param filePath 目标文件路径
     * @return true: 是文件, false: 不是文件
     * @throws SftpException SftpException
     */
    public boolean isFile(String filePath) throws SftpException {
        SftpATTRS attrs = getAttrs(filePath);
        return !attrs.isDir() && !attrs.isLink();
    }

    /**
     * 判断目录或文件是否存在
     * @param path 目标路径
     * @return true: 存在, false: 不存在
     * @throws SftpException SftpException
     */
    public boolean isExist(String path) throws SftpException {
        SftpATTRS attrs = getAttrs(path);
        return  (attrs != null);
    }

    private static final String NO_SUCH_FILE = "no such file";
    /**
     * 获取远程文件或目录属性
     * @param path 目标路径
     * @return 文件或目录属性
     * 注: 如果文件或目录不存在则返回 null
     * @throws SftpException SftpException
     */
    public SftpATTRS getAttrs(String path) throws SftpException {
        try {
            return sftp.lstat(path);
        } catch (SftpException e) {
            if (NO_SUCH_FILE.equalsIgnoreCase(e.getMessage())) {
                return null;
            }
            throw e;
        }
    }

    /**
     * 查看当前所处目录
     *
     * @return 当前目录字串
     * @throws SftpException SftpException
     */
    public String pwd() throws SftpException {
        return sftp.pwd();
    }

    /**
     * 切换目录
     * @param path 路径
     * @throws SftpException SftpException
     */
    public void cd(String path) throws SftpException {
        sftp.cd(path);
    }

    /**
     * 创建目录
     * @param directory 目标路径
     * @throws SftpException SftpException
     */
    public void mkdir(String directory) throws SftpException {
        if (!isDir(directory)) {
            sftp.mkdir(directory);
        }
    }

    /**
     * 创建多级目录
     * @param createPath 目标路径
     * @throws SftpException SftpException
     */
    public void mkdirs(String createPath) throws SftpException {
        if (isDir(createPath)) {
            return;
        }
        // mkdir 命令不能创建多级目录, 所以要先根据文件分隔符分割成单个目录组
        String[] pathArry = createPath.trim().split(FileUtil.DEF_LINE_SEPARATOR);
        StringBuilder filePath = null;
        // 如果是绝对路径会以"/"开头, 此时分割出的数组首位是空串应替换成"/"
        if (pathArry[0].isEmpty()) {
            filePath = new StringBuilder(FileUtil.DEF_LINE_SEPARATOR);
        } else {
            filePath = new StringBuilder();
        }
        for (String pathNode : pathArry) {
            if (pathNode == null || pathNode.isEmpty()) {
                continue;
            }
            filePath.append(pathNode);
            String path = filePath.toString();
            mkdir(path);
            filePath.append(FileUtil.DEF_LINE_SEPARATOR);
        }
    }

    /**
     * 给目录授权
     * @param permsion 授权值
     * @param directory 目标路径
     * @throws SftpException SftpException
     */
    public  void chmod(int permsion, String directory) throws SftpException {
        sftp.chmod(permsion, directory);
    }

    /**
     * 重命名文件或者目录 ,移动文件或者目录
     *
     * @param oldpath 旧文件或目录
     * @param newpath 新文件或目录
     * @throws SftpException SftpException
     */
    public void rename(String oldpath, String newpath) throws SftpException {
        sftp.rename(oldpath, newpath);
    }

    /**
     * 功能说明:打开指定目录, 如果目录不存在则创建
     *
     * @param directory 目标路径
     * @throws SftpException SftpException
     */
    public void openDir(String directory) throws SftpException {
        if (directory == null || directory.isEmpty()) {
            throw new NullPointerException("directory is null");
        }
        if (!isDir(directory)) {
            mkdirs(directory);
        }
        sftp.cd(directory);
    }

    /**
     * 改变目录用户组
     *
     * @param gid 组名
     * @param path 文件或目录的路径
     * @throws SftpException SftpException
     */
    public void chgrp(Integer gid, String path) throws SftpException {
        sftp.chgrp(gid, path);
    }

    /**
     * 删除文件
     * 删除当前目录下的文件
     *
     * @param deleteFile 要删除的文件
     * @throws SftpException SftpException
     */
    public void delete(String deleteFile) throws SftpException {
        sftp.rm(deleteFile);
    }

    /**
     * 删除文件
     * 不能使用全路径删除, 先CD到文件所在目录, 删除完毕后在CD回原目录
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @throws SftpException SftpException
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        String pwd = pwd();
        sftp.cd(directory);
        sftp.rm(deleteFile);
        sftp.cd(pwd);
    }

    /**
     * 将本地文件名为 filePath 的文件上传到目标服务器，
     *
     * @param filePath 本地文件路径
     * @param remotePath 远程文件路径
     *                   注: remotePath 可以是目录, 也可以是文件
     *                   如果是目录则按照原文件名上传至 remotePath 目录
     *                   如果是文件则按照指定的文件名上传
     * @throws SftpException SftpException
     */
    public void uploadFile(String filePath, String remotePath) throws SftpException {
        sftp.put(filePath, remotePath);
    }

    /**
     * 按照指定模式上传文件
     * @param filePath 本地文件路径
     * @param remotePath 远程文件路径
     * @param mode 文件传输模式
     * @see SftpMode
     * @throws SftpException SftpException
     */
    public void uploadFile(String filePath, String remotePath,
                           SftpMode mode) throws SftpException {
        sftp.put(filePath, remotePath, mode.getMode());
    }

    /**
     * 文件上传
     * 提供回调函数实时反馈上传进度
     * @param filePath 本地文件路径
     * @param remotePath 远程路径
     * @param monitor 回调函数
     * @throws SftpException SftpException
     */
    public void uploadFile(String filePath, String remotePath,
                           SftpProgressMonitor monitor) throws SftpException {
        sftp.put(filePath, remotePath, monitor);
    }

    /**
     * 上传文件流到远程服务器
     * @param fileIo 文件流
     * @param remoteFilePath 远程文件路径, 必须是包含文件名的完整路径, 不能是目录
     * @throws SftpException SftpException
     */
    public void uploadFile(InputStream fileIo, String remoteFilePath) throws SftpException {
        if (isDir(remoteFilePath)){
            throw new IllegalAccessError(remoteFilePath + "is directory");
        }
        sftp.put(fileIo, remoteFilePath);
    }


    /**
     * 上传文件流到远程目录
     * 该方法返回一个输出流，可以向该输出流中写入数据，最终将数据传输到目标服务器，目标文件名为 remoteFilePath
     * remoteFilePath 不能为目录。
     * 采用默认的传输模式：OVERWRITE
     * @param  remoteFilePath 远程文件地址
     * @return 文件输出流
     * @throws SftpException SftpException
     */
    public OutputStream uploadFile(String remoteFilePath) throws SftpException {
        if (isDir(remoteFilePath)) {
            throw new IllegalAccessError("remoteFilePath is not fount");
        }
        return sftp.put(remoteFilePath);
    }

    /**
     * 上传文件到指定的远程目录
     * @param localFile 本地文件
     * @param remoteFilePath 远程文件地址
     * @throws SftpException SftpException
     */
    public void uploadFile(File localFile, String remoteFilePath) throws SftpException {
        if (!localFile.isFile()){
            throw new IllegalAccessError("localFile is not found");
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(localFile);
            sftp.put(in,remoteFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 批量上传本地目录下的文件到远程目录
     * @param directory 本地目录
     * @param remoteDir 远程目录
     * @throws SftpException SftpException
     */
    public void uploadFiles(String directory, String remoteDir) throws SftpException {
        uploadFiles(directory,remoteDir,null);
    }

    /**
     * 批量上传本地目录下的文件到远程目录
     * @param directory 本地目录
     * @param remoteDir 远程目录
     * @param filter 过滤器, 可以为空, 为空则不做过滤
     * @throws SftpException SftpException
     */
    public void uploadFiles(String directory, String remoteDir, FileFilter filter) throws SftpException {
        File filePath = new File(directory);
        if (!filePath.isDirectory() || !filePath.exists()){
            return;
        }
        File[] files = null;
        if (filter == null){
            files = filePath.listFiles();
        }
        else{
            files = filePath.listFiles(filter);
        }
        if (files != null && files.length > 0){
            for (File file : files) {
                uploadFile(file,remoteDir);
            }
        }
    }

    /**
     * 下载文件
     * @param remoteFilePath 远程文件地址
     * @param localFilePath 本地文件地址
     * @throws SftpException SftpException
     */
    public void download(String remoteFilePath, String localFilePath) throws SftpException {
        sftp.get(remoteFilePath, localFilePath);
    }

    /**
     * 下载文件，下载过程中采用重命名防止被其他程序误处理
     *
     * @param remotePath  远程目录
     * @param fileName      文件名
     * @param savePath      保存目录
     * @param suffixPattren 下载中文件后缀名
     * @return boolean
     */
    public  boolean downloadAsnFile(String remotePath, String fileName,
                                    String savePath, String suffixPattren) {
        boolean status;
        FileOutputStream fileOutputStream = null;
        try {
            File fileDir = new File(savePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File tempFile = new File(FileUtil.unite(savePath, fileName + suffixPattren));
            fileOutputStream = new FileOutputStream(tempFile);

            sftp.get(FileUtil.unite(remotePath, fileName), fileOutputStream);
            File file = new File(FileUtil.unite(savePath, fileName));
            status = tempFile.renameTo(file);
        } catch (Exception e) {
            status = false;
            log.error("下载文件异常，原因：{}", e.getMessage());
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭文件{}出错", fileName);
            }
        }

        return status;
    }

    /**
     * 列出指定目录下的文件
     * 失败时返回空list
     *
     * @param directory 要列出的目录
     * @return 文件名列表
     * @throws SftpException SftpException
     */
    public List<String> listFileNames(String directory) throws SftpException {

        List<String> ftpFileNameList = new ArrayList<>();

        if (directory != null && !directory.isEmpty()) {
            Vector<ChannelSftp.LsEntry> sftpFile = sftp.ls(directory);
            for (ChannelSftp.LsEntry item : sftpFile) {
                ftpFileNameList.add(item.getFilename());
            }
        }
        return ftpFileNameList;
    }

    public List<ChannelSftp.LsEntry> listFiles(String directory) throws SftpException {
        if (directory == null || directory.isEmpty()) {
            throw new IllegalAccessError("directory is null!");
        }
        Vector<ChannelSftp.LsEntry> sftpFile = sftp.ls(directory);
        return new ArrayList<>(sftpFile);
    }

    /**
     * 打开指定文件名的文件, 返回InputStream
     * 失败返回 null
     *
     * @param filePath 要打开的文件名
     * @return io流
     * @throws SftpException SftpException
     */
    public InputStream openFile(String filePath) throws SftpException {
        return sftp.get(filePath);
    }

    /**
     * 复制文件, 复制完成后文件名不变
     *
     * @param fromPath 文件所在路径
     * @param toPath   要复制到的目标路径
     * @param fileName 文件名
     * @throws SftpException SftpException
     */
    public void copyfile(String fromPath, String toPath, String fileName)
            throws SftpException, IOException {

        String formFilePath = FileUtil.unite(fromPath, fileName);
        String toFilePath = FileUtil.unite(toPath, fileName);
        copyfile(formFilePath, toFilePath);
    }

    /**
     * 复制文件
     * 如果指定的目标文件名和原始文件名不同, 复制完成后文件名会改变
     *
     * @param from 原始文件路径, 必须包含文件名
     * @param to   目标文件路径, 必须包含文件名
     * @throws SftpException SftpException
     */
    public void copyfile(String from, String to) throws SftpException, IOException {
        try(InputStream in = sftp.get(from);
            OutputStream os = sftp.put(to)) {
            int len;
            while (-1 < (len = in.read())){
                os.write(len);
            }
            os.flush();
        }
    }

    void disconnect(){
        sftp.disconnect();
    }

    @Override
    public void close() {
        SftpPooledFactory.closeSftp(this);
    }
}