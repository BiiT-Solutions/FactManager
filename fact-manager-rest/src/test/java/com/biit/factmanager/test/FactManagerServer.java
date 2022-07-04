package com.biit.factmanager.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.biit.factmanager"})
@ConfigurationPropertiesScan({"com.biit.factmanager.persistence.configuration"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
public class FactManagerServer {

	public static void main(String[] args) {
		SpringApplication.run(FactManagerServer.class, args);
	}

}
