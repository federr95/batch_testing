package com.example.uploadCSVtoH2;

import com.example.uploadCSVtoH2.simple_read.SimpleRead;
import com.example.uploadCSVtoH2.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class UploadCsVtoH2Application {

	@Autowired
	SimpleRead simpleRead;

	public static void main(String[] args) {
		SpringApplication.run(UploadCsVtoH2Application.class, args);
	}

	@Bean
	public CommandLineRunner run(EvidenceRepository evidenceRepository) throws Exception {
		return (String[] args) -> {
			simpleRead.readFile(evidenceRepository);
		};
	}

}

