package com.mims.medicalinternsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicalInternSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalInternSystemApplication.class, args);
	}

}
