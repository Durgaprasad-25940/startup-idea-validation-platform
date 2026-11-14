package com.startup.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * StartupValidatorApplication is the entry point class for our Spring Boot backend.
 * 
 * `@SpringBootApplication` annotation enables three main features:
 * 1. `@EnableAutoConfiguration`: Enables Spring Boot's auto-configuration mechanism.
 * 2. `@ComponentScan`: Scans the package where this class is located and its sub-packages for Spring components (Controllers, Services, Repositories, etc.).
 * 3. `@SpringBootConfiguration`: Declares this class as a configuration class.
 */
@SpringBootApplication
public class StartupValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartupValidatorApplication.class, args);
    }
}
