package me.meilon.sftp.autoconfigure;

import me.meilon.sftp.client.SftpClientFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringClientApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // Set ApplicationContext into SpringCloudRegistryFactory before Dubbo Service
        // Register
        SftpClientFactory.setApplicationContext(applicationContext);
    }
}
