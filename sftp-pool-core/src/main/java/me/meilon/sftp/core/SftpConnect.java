package me.meilon.sftp.core;


import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import me.meilon.sftp.core.exception.SftpConnectException;
import me.meilon.sftp.core.utils.Base64Util;
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

    private String ftpName;

    /**
     * 用于标识此链接是否通过链接池创建
     */
    private boolean isPool;

    protected SftpConnect(String ftpName, ChannelSftp sftp) {
        this.ftpName = ftpName;
        this.sftp = sftp;
    }

    public void setFtpName(String ftpName) {
        this.ftpName = ftpName;
    }

    public boolean isPool() {
        return isPool;
    }

    public void setPool(boolean pool) {
        isPool = pool;
    }

    public String getFtpName(){
        return this.ftpName;
    }


    public void connect(){
        try {
            Session session = sftp.getSession();
            if (!session.isConnected()){
                session.connect();
            }
            if (!sftp.isConnected()){
                sftp.connect();
            }
        } catch (JSchException e) {
            throw new SftpConnectException(e);
        }
    }

    /**
     * 检查连接状态
     *
     * @return true: 连接正常
     * false: 连接断开
     */
    public boolean isConnected() {
        if (sftp.isConnected()) {
            return true;
        }
        log.info("SFTP {} 连接被断开", ftpName);
        return false;
    }

    /**
     * 查看当前所处目录
     *
     * @return 当前目录字串
     */
    public String pwd() {
        try {
            return sftp.pwd();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 功能说明:打开指定目录
     *
     * @param directory
     * @return boolean
     */
    public  boolean openDir(String directory) {
        if (!isConnected()){
            return false;
        }
        if (directory == null || directory.isEmpty()) {
            log.error("sftp 打开目录失败: 目录名不能为空!");
            return false;
        }
        try {
            if (!isDirExist(directory)) {
                sftp.mkdir(directory);
            }
            sftp.cd(directory);
            return true;
        } catch (SftpException e) {
            log.error("sftp 打开目录错误: ", e);
            return false;
        }
    }

    /**
     * 上传文件
     * 上传本地文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件名
     */
    public boolean upload(String directory, String uploadFile) {
        log.info("sftp upload file {} to {}", uploadFile, directory);

        boolean status = false;
        File file = new File(uploadFile);

        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            boolean isOpenDir = this.openDir(directory);

            log.info("访问文件夹是否成功{}", isOpenDir);
            if (!isOpenDir) {
                log.error("上传文件失败, 无法访问目标文件夹!");
                return false;
            }
            log.info("文件开始上传文件流,文件名: {}", file.getName());
            sftp.put(fileInputStream, file.getName());
            status = true;

        } catch (IOException | SftpException e) {
            e.printStackTrace();
        }
        return status;
    }



    /**
     * 上传图片
     * 上传base64Str 格式的图片
     *
     * @param base64Str base64编码必填（去掉"data:image/jpeg;base64,"头）
     * @param directory 目录，必填(device=设备图片目录，inspection=巡检图片目录)
     * @param fileName  文件名可以为空（扩展名建议jpg）
     */
    public  String uploadFile(String base64Str, String directory, String fileName) {
        if (base64Str == null || base64Str.isEmpty()) {
            throw new IllegalArgumentException("图片 base64 编码不能为空");
        }

        try (InputStream inputStream = Base64Util.baseToInputStream(base64Str)) {

            if (this.openDir(directory)) {
                if (fileName == null || fileName.isEmpty()) {
                    fileName = UUID.randomUUID().toString().toUpperCase(Locale.ENGLISH) + ".jpg";
                }
                // 图片服务器目录：device=设备图片目录，inspection=巡检图片目录，文件名=UUID
                // 目标文件名
                String dst = FileUtil.unite(directory, fileName);
                sftp.put(inputStream, dst, ChannelSftp.OVERWRITE);

            }
        } catch (Exception e) {
            log.error("上传图片错误: ", e);
        }
        return fileName;
    }

    /**
     * 下载文件
     *
     * @param directory    下载文件所在路径目录
     * @param downFileName 下载的文件名称
     * @param savePath     保存到本地的路径目录
     */
    public  boolean download(String directory, String downFileName, String savePath) {

        log.info("sftp 下载 {} to {}", FileUtil.unite(directory, downFileName), savePath);
        boolean status = false;
        File file = null;
        FileOutputStream fileOutputStream = null;

        try {
            if (openDir(directory)) {
                if (FileUtil.isExistDir(savePath)) {
                    String saveFile = FileUtil.unite(savePath, downFileName);
                    file = new File(saveFile);
                    fileOutputStream = new FileOutputStream(file);
                    sftp.get(downFileName, fileOutputStream);
                    status = true;
                }
            }
        } catch (Exception e) {
            log.error("sftp 下载 {} 失败: {}", downFileName, e);
            this.close();
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                log.error("sftp 下载文件, 关闭输出流失败");
                status = false;
            }
        }
        return status;
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件名称
     * @param localFile    本地文件名称
     * @param saveDir      存在本地的目录
     */
    public  boolean download(String directory, String downloadFile, String localFile, String saveDir)
            throws IOException {

        boolean status = false;
        File file = null;
        FileOutputStream fileOutputStream = null;

        try {
            if (openDir(directory)) {
                File fileDir = new File(saveDir);

                if (isExistsDir(fileDir)) {

                    file = new File(saveDir + localFile);
                    fileOutputStream = new FileOutputStream(file);
                    sftp.get(downloadFile, fileOutputStream);
                    status = true;
                }
            }

        } catch (Exception e) {
            log.error("", e);
            this.close();
        } finally {
            if (null != fileOutputStream) {
                fileOutputStream.close();
            }
        }

        return status;
    }

    /**
     * 下载文件，下载过程中采用重命名防止被其他程序误处理
     *
     * @param downloadPath  下载目录
     * @param fileName      文件名
     * @param savePath      保存目录
     * @param suffixPattren 下载中文件后缀名
     * @return boolean
     */
    public  boolean downloadAsnFile(String downloadPath, String fileName, String savePath, String suffixPattren) {
        boolean status = true;
        FileOutputStream fileOutputStream = null;
        try {
            File fileDir = new File(savePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File tempFile = new File(FileUtil.unite(savePath, fileName + suffixPattren));
            fileOutputStream = new FileOutputStream(tempFile);

            sftp.get(fileName, fileOutputStream);
            File file = new File(FileUtil.unite(savePath, fileName));
            tempFile.renameTo(file);
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
     * 判断指定文件夹是否存在, 不存在则创建
     *
     * @param file 指定文件夹
     * @author RenZhengGuo 2016年8月13日 下午5:29:37
     */
    private  boolean isExistsDir(File file) {
        boolean mkdir = false;
        // 如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            log.info("目录不存在，创建目录");
            mkdir = file.mkdirs();
        }
        return mkdir;
    }

    /**
     * @param file
     * @author RenZhengGuo 2016年8月13日 下午5:29:37
     */
    public  void cd(String file) {
        // 如果文件夹不存在则创建
        try {
            sftp.cd(file);
        } catch (SftpException e) {
            createDir(file);
//            chmod(Integer.parseInt("777", 8), file);
        }

    }

    /**
     * 创建目录
     *
     * @param file
     * @return
     */
    public  boolean mkdir(String file) {
        if (isDirExist(file)) {
            return true;
        }
        try {
            sftp.mkdir(file);
            return true;
        } catch (SftpException e) {
            log.error("创建文件夹出错！ ", e);
            return false;
        }
    }

    /**
     * 给目录授权
     *
     * @param permsion
     * @param file
     */
    public  void chmod(int permsion, String file) {
        try {
            sftp.chmod(permsion, file);
        } catch (SftpException e) {
            log.info("为文件：{}授权失败！", file);
        }
    }

    /**
     * 创建一个文件目录
     */
    public  void createDir(String createpath) {
        try {
            if (isDirExist(createpath)) {
                sftp.cd(createpath);
                return;
            }
            // mkdir 命令不能创建多级目录, 所以要先根据文件分隔符分割成单个目录组
            String[] pathArry = createpath.trim().split(FileUtil.DEF_LINE_SEPARATOR);
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

                if (!isDirExist(path)) {
                    sftp.mkdir(path);
                }
                filePath.append(FileUtil.DEF_LINE_SEPARATOR);
            }
            sftp.cd(createpath);
        } catch (SftpException e) {
            log.info("创建路径错误：" + createpath);
        }
    }

    /**
     * 判断目录是否存在
     */
    public  boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS attrs = sftp.lstat(directory);
            isDirExistFlag = true;
            return attrs.isDir();
        } catch (Exception e) {
            if ("no such file".equals(e.getMessage().toLowerCase())) {
                isDirExistFlag = false;
            } else {
                this.close();
            }
        }
        return isDirExistFlag;
    }

    /**
     * 判断文件是否存在
     */
    public  boolean fileExist(String directory, String fileName) {
        boolean isDirExistFlag = false;
        try {
            Vector<ChannelSftp.LsEntry> vector = sftp.ls(directory);
            if (vector != null && !vector.isEmpty()) {
                Iterator<ChannelSftp.LsEntry> iterator = vector.iterator();
                while (iterator.hasNext()) {
                    ChannelSftp.LsEntry f = iterator.next();
                    if (f.getAttrs().isDir()) {
                        continue;
                    }
                    if (fileName.equals(f.getFilename())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            if ("no such file".equals(e.getMessage().toLowerCase())) {
                isDirExistFlag = false;
            } else {
                this.close();
            }
        }
        return isDirExistFlag;
    }

    /**
     * 删除文件
     * 不能使用全路径删除, 先CD到文件所在目录, 删除完毕后在CD回原目录
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public  boolean delete(String directory, String deleteFile) {

        checkClient();
        log.info("sftp del {}/{}", directory, deleteFile);
        String pwd = pwd();
        boolean status = false;
        try {

            sftp.cd(directory);
            sftp.rm(deleteFile);
            status = true;
            sftp.cd(pwd);

        } catch (Exception e) {

            log.error("sftp 删除文件错误: ", e);
            this.close();
        }
        return status;
    }

    /**
     * 删除文件
     * 删除当前目录下的文件
     *
     * @param deleteFile 要删除的文件
     */
    public  boolean delete(String deleteFile) {

        checkClient();
        log.info("sftp del {}", deleteFile);
        boolean status = false;
        try {
            sftp.rm(deleteFile);
            status = true;

        } catch (Exception e) {

            log.error("sftp 删除文件错误: ", e);
            this.close();
        }
        return status;
    }

    /**
     * 列出指定目录下的文件
     * 失败时返回空list
     *
     * @param directory 要列出的目录
     * @return 文件名列表
     */
    public List<String> listFiles(String directory) {

        checkClient();
        log.info("sftp ls {}", directory);

        List<String> ftpFileNameList = new ArrayList<>();

        if (directory != null && !directory.isEmpty()) {
            try {
                Vector<ChannelSftp.LsEntry> sftpFile = sftp.ls(directory);
                for (ChannelSftp.LsEntry item : sftpFile) {
                    ftpFileNameList.add(item.getFilename());
                }
            } catch (SftpException e) {
                log.error("sftp 获取文件列表错误! {}", e.getMessage());
                this.close();
            }
        }
        return ftpFileNameList;
    }


    /**
     * 改变目录用户组
     *
     * @param gid
     * @param path
     * @return boolean
     */
    public  boolean chgrp(Integer gid, String path) {
        try {
            sftp.chgrp(gid, path);
            return true;
        } catch (SftpException e) {
            log.info("改变用户组失败:{}", e.getMessage());
            return false;
        }

    }

    /**
     * 打开指定文件名的文件, 返回InputStream
     * 失败返回 null
     *
     * @param filePath 要打开的文件名
     * @return io流
     */
    public  InputStream openFile(String filePath) {
        log.info("sftp open file {}", filePath);
        InputStream inputStream = null;
        try {
            inputStream = sftp.get(filePath);
            return inputStream;
        } catch (SftpException e) {
            log.error("打开文件错误: ", e);
            this.close();
            return null;
        }
    }

    /**
     * 重命名文件或者目录 ,移动文件或者目录
     *
     * @param oldpath 旧文件或目录
     * @param newpath 新文件或目录
     */
    public  boolean rename(String oldpath, String newpath) {
        log.info("sftp mv {} {}", oldpath, newpath);
        try {
            sftp.rename(oldpath, newpath);
            return true;
        } catch (Exception e) {
            log.error("SFTP移动文件错误: ", e);
            return false;
        }
    }



    /**
     * 复制文件, 复制完成后文件名不变
     *
     * @param fromPath 文件所在路径
     * @param toPath   要复制到的目标路径
     * @param fileName 文件名
     * @throws SftpException,IOException ""
     */
    public  void copyfile(String fromPath, String toPath, String fileName)
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
     * @throws SftpException,IOException ""
     */
    public  void copyfile(String from, String to) throws SftpException, IOException {

        log.info("sftp copy file {} to {}", from, to);
        InputStream tInputStream = null;

        ByteArrayOutputStream baos = null;
        InputStream nInputStream = null;
        try {
            tInputStream = sftp.get(from);

            //拷贝读取到的文件流
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = tInputStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            nInputStream = new ByteArrayInputStream(baos.toByteArray());

            sftp.put(nInputStream, to);
        } catch (Exception e) {
            log.error("sftp复制文件错误: ", e);
        } finally {
            if (null != nInputStream) {
                nInputStream.close();
            }
            if (null != baos) {
                baos.close();
            }
            if (null != tInputStream) {
                tInputStream.close();
            }
        }
    }

    /**
     * 检查连接状态, 连接断开则自动重连
     * 重连失败抛出异常
     */
    public void checkClient() {
        if (!this.isConnected()) {
            this.connect();
        }
    }

    public void exit(){
        sftp.exit();
    }

    @Override
    public void close() {
        SftpPooledFactory.closeSftp(this);
    }
}