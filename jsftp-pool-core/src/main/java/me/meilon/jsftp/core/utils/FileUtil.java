package me.meilon.jsftp.core.utils;


import java.io.File;

/**
 * 文件工具类
 * @author meilon
 */
public class FileUtil {

    /**
     * 默认文件分隔符
     * windows中分隔符有所不同, 但也能识别因此作为默认分隔符使用
     */
    final static public String DEF_LINE_SEPARATOR= "/";
    /**
     * windows 默认文件分隔符, 在linux系统中不能使用
     */
    final static public String WIN_LINE_SEPARATOR = "\\";
    /**
     * 获取当前系统默认换行符
     */
    final static public String LINE_SEPARATOR = System.getProperty("line.separator", "/n");
    /**
     * 当前系统默认文件分隔符
     */
    final static public String FILE_SEPARATOR = System.getProperty("file.separator", DEF_LINE_SEPARATOR);


    /**
     * 判断多级路径是否存在，不存在就创建
     * 如果存在则再判断是文件还是目录
     * @param path 目标路径
     * @return true 目录已存在, 或创建成功, false: 目标为文件, 或创建目录失败;
     */
    public static boolean isExistDir(String path) {
        boolean res = false;
        File file = new File(path);
        int exist = isExist(file);
        if (exist < 0){
            return createDir(path);
        }else if (exist == 0){
            res = true;
        }
        return res;
    }

    /**
     * 判断目标路径类型
     * @param path 目标路径
     * @return  -1 : 目标不存在, 0: 目标为目录, 1: 目标为文件
     */
    public static int isExist(String path){
        File file = new File(path);
        return isExist(file);

    }

    /**
     * 判断目标对象类型
     * @param file 目标对象
     * @return  -1 : 目标不存在, 0: 目标为目录, 1: 目标为文件
     */
    public static int isExist(File file){
        int res = -1;
        if (file.exists()){
            if (file.isFile()){
                res = 1;
            }
            if (file.isDirectory()){
                res = 0;
            }
        }
        return res;
    }

    /**
     * 创建目录, 如果目标已经存在则直接返回true
     * 创建失败返回false
     * @param path 目标路径
     * @return true 成功, false 失败
     */
    public static boolean createDir(String path){
        File file = new File(path);
        return createDir(file);
    }

    /**
     * 创建目录, 如果目标已经存在则直接返回true
     * 创建失败返回false
     * @param file 目标路径
     * @return true 成功, false 失败
     */
    public static boolean createDir(File file){
        boolean res = false;
        int isF = isExist(file);
        if (isF < 0){
            // mkdirs可以创建多级目录
            res = file.mkdirs();
        }
        if (isF == 0){
            res = true;
        }
        return res;
    }

    /**
     * 用于拼接路径和文件名
     *
     * @param filePaths 文件路径
     * @return 完整路径
     */
    public static String unite(String... filePaths){
        return unite(true, filePaths);
    }

    /**
     * 拼接文件路径或目录路径
     * isFilePath 为 true时指定为拼接文件路径, 此时路径最后不会拼接文件分隔符
     * isFilePath 为 false时指定为拼接目录路径, 此时路径最后会拼接文件分隔符
     * 注: 目录名不能为null, 否则会忽略此目录
     * @param isFilePath 是否是文件路径
     * @param dirs 目录
     * @return 完整路径
     */
    public static String unite(boolean isFilePath, String... dirs){
        if (null == dirs || dirs.length == 0){
            return null;
        }
        StringBuilder pathStr = new StringBuilder();
        for (int i = 0; i < dirs.length; i++) {
            String dir = dirs[i];
            if (dir != null && !dir.isEmpty()){
                pathStr.append(dir);
                String wei = dir.substring(dir.length() - 1);
                // 判断目录名最后一位是否已经有文件分隔符, 没有则拼接分隔符
                if (!DEF_LINE_SEPARATOR.equals(wei) && !WIN_LINE_SEPARATOR.equals(wei)){
                    if (i == dirs.length - 1 && isFilePath){
                        continue;
                    }
                    pathStr.append(DEF_LINE_SEPARATOR);
                }
            }
        }
        return pathStr.toString();
    }

}
