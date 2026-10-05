package com.FinalProject.dynaicResultAnalyzer;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DynamicResultAnalyzerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DynamicResultAnalyzerApplication.class, args);
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press 'exit' to exit the application...");
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Shutting down gracefully...");
                SpringApplication.exit(context);
                break;
            }
        }
    }
}
