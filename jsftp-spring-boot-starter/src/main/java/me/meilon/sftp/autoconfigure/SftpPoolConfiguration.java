package me.meilon.sftp.autoconfigure;


import me.meilon.sftp.client.SftpClientFactory;
import me.meilon.sftp.core.SftpPool;
import me.meilon.sftp.core.SftpPooledFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * sftp pool 配置对象
 * @author meilon
 */
@Configuration
public class SftpPoolConfiguration {

    @Bean
    public SftpPoolProperties sftpPoolProperties(){
        return new SftpPoolProperties();
    }

    @Bean
    public SftpPooledFactory sftpPooledFactory(SftpPoolProperties properties){
        return new SftpPooledFactory(properties.getSftpConnConfigMap(), properties);
    }

    @Bean
    public SftpPool sftpPool(SftpPooledFactory factory){
        SftpClientFactory.init(factory);
        return factory.getSftpPool();
    }
}
