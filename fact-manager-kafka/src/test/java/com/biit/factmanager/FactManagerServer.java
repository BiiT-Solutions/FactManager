package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.biit.factmanager", "com.biit.kafka"})
public class FactManagerServer {

    public static void main(String[] args) {
        SpringApplication.run(FactManagerServer.class, args);
    }

//    @Bean(name="entityManagerFactory")
//    public LocalSessionFactoryBean entityManagerFactory() {
//        return new LocalSessionFactoryBean();
//    }

}
