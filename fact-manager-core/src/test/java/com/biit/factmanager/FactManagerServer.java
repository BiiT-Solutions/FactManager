package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@SpringBootApplication
@ComponentScan({"com.biit.factmanager"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
@Service

public class FactManagerServer {


	public static void main(String[] args) {
		SpringApplication.run(FactManagerServer.class, args);
	}

}
