package com.example.agency.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.agency.models")
@ComponentScan(basePackages = {
		"com.example.agency.grpc", // Inclure les implémentations gRPC
		"com.example.agency.services", // Services métier et gestion des données
		"com.example.agency.Repositories", // Repositories JPA
		"com.example.agency.utils" // Classe utilitaire pour les conversions
})
@EnableJpaRepositories(basePackages = "com.example.agency.Repositories")
public class AgencyApplication {
	public static void main(String[] args) {
		SpringApplication.run(AgencyApplication.class, args);
		System.out.println("Agency gRPC Service is running...");
	}
}
