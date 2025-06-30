package me.dineka.books_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class BooksServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooksServiceApplication.class, args);
	}

}
