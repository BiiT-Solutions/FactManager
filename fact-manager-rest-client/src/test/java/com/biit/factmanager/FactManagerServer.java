package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.biit.factmanager", "com.biit.server"})
@ConfigurationPropertiesScan({"com.biit.factmanager"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
public class FactManagerServer {

    public static void main(String[] args) {
        SpringApplication.run(FactManagerServer.class, args);
    }

}
