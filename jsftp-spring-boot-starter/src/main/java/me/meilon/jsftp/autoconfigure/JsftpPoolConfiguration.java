package me.meilon.jsftp.autoconfigure;


import lombok.extern.slf4j.Slf4j;
import me.meilon.jsftp.client.JsftpClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * sftp pool 配置对象
 * @author meilon
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(JsftpPoolProperties.class)
public class JsftpPoolConfiguration {


    @Bean
    public JsftpClientFactory sftpPooledFactory(JsftpPoolProperties properties){
        return new JsftpClientFactory(properties.getSftpConnConfigMap(), properties);
    }

}
