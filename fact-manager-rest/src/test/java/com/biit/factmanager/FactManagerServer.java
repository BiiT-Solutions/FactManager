package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.biit.factmanager", "com.biit.kafka", "com.biit.usermanager.client", "com.biit.server", "com.biit.messagebird"})
public class FactManagerServer {

    public static void main(String[] args) {
        SpringApplication.run(FactManagerServer.class, args);
    }

}
