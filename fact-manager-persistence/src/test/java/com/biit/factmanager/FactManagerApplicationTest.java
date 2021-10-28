package com.biit.factmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.biit.factmanager")
public class FactManagerApplicationTest {

	public static void main(String[] args) {
		SpringApplication.run(FactManagerApplicationTest.class, args);
	}

}
