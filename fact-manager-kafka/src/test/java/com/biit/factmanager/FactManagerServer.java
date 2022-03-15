package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

@SpringBootApplication
@ComponentScan({"com.biit.factmanager"})
@EnableJpaRepositories({"com.biit.factmanager.persistence.repositories"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
@PropertySource("classpath:application.properties")
@Service
public class FactManagerServer {

    public static void main(String[] args) {
        SpringApplication.run(FactManagerServer.class, args);
    }

}
