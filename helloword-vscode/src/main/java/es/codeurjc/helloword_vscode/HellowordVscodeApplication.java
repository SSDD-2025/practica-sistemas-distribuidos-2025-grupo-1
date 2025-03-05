package es.codeurjc.helloword_vscode;

// Importing Spring Boot classes for bootstrapping the application
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Marks this class as the main entry point for the Spring Boot application
public class HellowordVscodeApplication {

    // Main method that launches the Spring Boot application
    public static void main(String[] args) {
        // Runs the Spring application with the specified class and command line arguments
        SpringApplication.run(HellowordVscodeApplication.class, args);
    }

}
