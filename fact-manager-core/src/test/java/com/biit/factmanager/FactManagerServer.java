package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.biit.factmanager", "com.biit.kafka"})
public class FactManagerServer {

	public static void main(String[] args) {
		SpringApplication.run(FactManagerServer.class, args);
	}

}
