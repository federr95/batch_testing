package com.example.uploadCSVtoH2;

import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.read_from_filesystem.ReadFromFileSystem;
import com.example.uploadCSVtoH2.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UploadCsVtoH2Application {

	@Autowired
	ReadFromFileSystem readFromFileSystem;

	public static void main(String[] args) {
		SpringApplication.run(UploadCsVtoH2Application.class, args);
	}

	/*
		Questo bean permette la creazione del processo di lettura del file tramite file system.
	 */
	/*@Bean
	public CommandLineRunner run(EvidenceRepository evidenceRepository) throws Exception {
		return (String[] args) -> {
			readFromFileSystem.readFile(evidenceRepository);
		};
	}*/

}
