package com.github.bondarevv23.memorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.github.bondarevv23.memorizer"})
public class MemorizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemorizerApplication.class, args);
	}

}
