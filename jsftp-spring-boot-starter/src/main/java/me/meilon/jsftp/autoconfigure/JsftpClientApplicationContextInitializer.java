package me.meilon.jsftp.autoconfigure;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author meilon
 */
public class JsftpClientApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        JsftpClientRegistryFactory.setApplicationContext(applicationContext);
    }
}
