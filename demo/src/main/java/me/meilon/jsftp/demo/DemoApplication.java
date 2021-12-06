package me.meilon.jsftp.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Bean
//    public JsftpClientFactory jsftpClientFactory(){
//        return new JsftpClientFactory();
//    }

}
