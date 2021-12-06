package me.meilon.jsftp.autoconfigure;


import me.meilon.jsftp.client.JsftpClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * sftp pool 配置对象
 * @author meilon
 */
@Configuration
@EnableConfigurationProperties(JsftpPoolProperties.class)
public class JsftpPoolConfiguration {


    @Bean
    public JsftpClientFactory jsftpClientFactory(JsftpPoolProperties properties){
        return new JsftpClientFactory(properties.getSftpConnConfigMap(), properties);
    }

}
