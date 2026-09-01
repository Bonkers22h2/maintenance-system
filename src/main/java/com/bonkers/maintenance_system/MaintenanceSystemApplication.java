package com.bonkers.maintenance_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaintenanceSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaintenanceSystemApplication.class, args);
	}

}
