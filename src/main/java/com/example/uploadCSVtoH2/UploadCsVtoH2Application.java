package com.example.uploadCSVtoH2;

import com.example.uploadCSVtoH2.batch_chunk_config.BatchChunkConfig;
import com.example.uploadCSVtoH2.ciphersuite.CipherSuite;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

// Posizionando l'annotazione nel punto d'ingresso del programma si riesce a fare in modo
// che i bean della classe BatchChunkConfig.java possano essere referenziati (siccome la instantiation,
// ovvero la creazione, viene fatta con l'annotazione @Configuration in BatchChunkConfig.java) senza che il job venga
// lanciato. Al contrario se messo nella classe BatchChunkConfig.java fa partire direttamente il job

@SpringBootApplication
@EnableBatchProcessing
public class UploadCsVtoH2Application {

	@Autowired
	BatchChunkConfig batchChunkConfig;

	@Autowired
	Job loadEvidenceJob;

	@Autowired
	JobLauncher jobLauncher;

	public static void main(String[] args) {
		SpringApplication.run(UploadCsVtoH2Application.class, args);
	}

	@Bean
	public void decryption() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

		// creation of the key
		CipherSuite cipherSuite = new CipherSuite("TEST", "BD7DB131AB0F353C",
				 "AES", 256);
		System.out.println("Cyper Suite elements: ");
		System.out.println("key       --> " + Base64.getEncoder().encodeToString(cipherSuite.getSecretKey().getEncoded()));
		System.out.println("Password  --> " + cipherSuite.getPassword());
		System.out.println("Salt      --> " + cipherSuite.getSalt());
		System.out.println("IV        --> " + cipherSuite.getIvParameterSpec().getIV());

		// encryption
		//CipherSuite.encryptFile("AES/CBC/PKCS5Padding", cipherSuite.getSecretKey(), cipherSuite.getIvParameterSpec());

		// decryption
		CipherSuite.decryptFile("AES/CBC/PKCS5Padding", cipherSuite.getSecretKey(), cipherSuite.getIvParameterSpec());

		System.out.println("decryption terminate ");

		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(loadEvidenceJob, jobParameters);
	}

}
