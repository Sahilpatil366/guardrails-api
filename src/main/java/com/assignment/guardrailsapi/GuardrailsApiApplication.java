package com.assignment.guardrailsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class GuardrailsApiApplication {

	public static void main(String[] args) {
		// Force UTC timezone BEFORE Spring Boot even starts initializing!
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(GuardrailsApiApplication.class, args);
	}

}