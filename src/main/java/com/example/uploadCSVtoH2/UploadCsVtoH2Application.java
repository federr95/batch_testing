package com.example.uploadCSVtoH2;

import com.example.uploadCSVtoH2.simple_read.SimpleRead;
import com.example.uploadCSVtoH2.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class UploadCsVtoH2Application {

	@Autowired
	SimpleRead simpleRead;

	private static final String SYM_ALGORITHM 	= "AES";
	private static final Integer SYM_KEY_SIZE 	= 128;

	public static void main(String[] args) {
		SpringApplication.run(UploadCsVtoH2Application.class, args);
	}

	@Bean
	public CommandLineRunner run(EvidenceRepository evidenceRepository) {
		return (String[] args) -> {
			//simpleRead.readFile(evidenceRepository);
		};
	}

	@Bean
	public static Key generateSymmetricKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(SYM_ALGORITHM);
		keyGenerator.init(SYM_KEY_SIZE);
		return keyGenerator.generateKey();
	}


}
