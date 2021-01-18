package me.meilon.jsftp.autoconfigure;


import me.meilon.jsftp.client.JsftpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * sftp pool 配置对象
 * @author meilon
 */
@Configuration
public class JsftpPoolConfiguration {

    @Bean
    public JsftpPoolProperties sftpPoolProperties(){
        return new JsftpPoolProperties();
    }

    @Bean
    public JsftpClientFactory sftpPooledFactory(JsftpPoolProperties properties){
        return new JsftpClientFactory(properties.getSftpConnConfigMap(), properties);
    }

}
