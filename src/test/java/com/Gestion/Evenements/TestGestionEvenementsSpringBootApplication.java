package com.Gestion.Evenements;

import org.springframework.boot.SpringApplication;

public class TestGestionEvenementsSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.from(GestionEvenementsSpringBootApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
