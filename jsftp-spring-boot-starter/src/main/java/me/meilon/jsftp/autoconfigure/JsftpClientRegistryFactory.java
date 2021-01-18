package me.meilon.jsftp.autoconfigure;

import me.meilon.jsftp.client.JsftpClientFactory;
import org.springframework.context.ConfigurableApplicationContext;


/**
 *
 * sftp 客户端注册工厂
 * 用于将 sftp client 工厂注册到一个静态工厂, 方便调用
 * @author meilon
 */
public class JsftpClientRegistryFactory {

    private static ConfigurableApplicationContext applicationContext;
    private static JsftpClientFactory jsftpClientFactory;
    private static boolean isInit = false;


    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        JsftpClientRegistryFactory.applicationContext = applicationContext;
    }

    private synchronized static void init(){
        if (isInit){
            return;
        }
        jsftpClientFactory = applicationContext.getBean(JsftpClientFactory.class);
        isInit = true;
    }

    private static void checkInit(){
        if (!isInit){
            init();
        }
    }

    public static JsftpClientFactory getJsftpClientFactory(){
        checkInit();
        return jsftpClientFactory;
    }

}
