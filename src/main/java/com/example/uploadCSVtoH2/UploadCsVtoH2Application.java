package com.example.uploadCSVtoH2;

import com.example.uploadCSVtoH2.batch_chunk_config.BatchChunkConfig;
import com.example.uploadCSVtoH2.ciphersuite.IncorrectPasswordException;
import com.example.uploadCSVtoH2.ciphersuite.PasswordAESCryptoUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//import static com.example.uploadCSVtoH2.ciphersuite.AES_256_CBC_OpenSSL.decrypt;
import static com.example.uploadCSVtoH2.ciphersuite.AES_256_CBC_OpenSSL.decrypt;
import static com.example.uploadCSVtoH2.ciphersuite.AES_256_CBC_OpenSSL.encrypt;

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

	@Value("${salt}")
	private String saltString;

	@Value("${iv}")
	private String ivString;

	@Value("${digest}")
	private String digestString;

	public static void main(String[] args) {
		SpringApplication.run(UploadCsVtoH2Application.class, args);
	}

	// ENCRYPTION this fase could be exploited in the archive part (la classe resources legge direttamente dalle risorse
	// mentre quando si crea un nuovo file bisogna dare il path a partire da src !?
	/*@Bean
	public void encryptionFolder() throws IOException {
		String secret = "PASSWORD";
		List<File> filesInFolder = Files.walk(Paths.get("C:/Users/feder/Downloads/batch_testing_v0.2/src/main/resources/files_to_be_encrypted"))
				.map(x -> x.toFile())
				.filter(x -> x.getName().endsWith(".csv"))
				.collect(Collectors.toList());
		for(File file : filesInFolder) {
			try {
				String path = "files_to_be_encrypted/";
				String path2 = "src/main/resources/encrypted_files/";
				String plainFileName = path + file.getName();
				String encryptedFileName = path2 + file.getName() + ".encrypted";
				Resource resource = new ClassPathResource(plainFileName);
				File plainFile = resource.getFile();
				File encryptedFile = new File(encryptedFileName);
				FileInputStream inputStream = new FileInputStream(plainFile);
				FileOutputStream outputStream = new FileOutputStream(encryptedFile);
				PasswordAESCryptoUtils passAESCryptoUtils = new PasswordAESCryptoUtils(secret);
				encrypt("PASSWORD", inputStream, outputStream,Base64.getDecoder().decode(ivString),
						Base64.getDecoder().decode(saltString));
				//encrypt("PASSWORD", inputStream, outputStream, passAESCryptoUtils.getIV(),
				//	passAESCryptoUtils.getSALT());
				//encrypt("PASSWORD", inputStream, outputStream, passAESCryptoUtils.getIvString().getBytes(StandardCharsets.US_ASCII),
				//		passAESCryptoUtils.getSaltString().getBytes(StandardCharsets.US_ASCII));
				System.out.println("file " + file.getName() + " has been encrypted!");
			} catch (IOException | NoSuchAlgorithmException exception) {
				throw new IncorrectPasswordException();
			}
		}

	}*/

	// DECRYPTION
	@Bean
	public void decryptionFolder() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

		String secret = "";
		boolean check = false;
		PasswordAESCryptoUtils passAESCryptoUtils = new PasswordAESCryptoUtils();

		while (!check) {
			System.out.println("Enter password to decrypt: ");
			Scanner scanIn = new Scanner(System.in);
			secret = scanIn.nextLine();
			String digestPassword = Base64.getEncoder().encodeToString(digestPassword(secret));
			if (!digestPassword.equals(digestString)){
				System.out.println("incorrect password");
			} else {
				check = true;
			}
		}

		System.out.println("processing...");

		List<File> filesInFolder = Files.walk(Paths.get("C:/Users/feder/Downloads/batch_testing_v0.2/src/main/resources/encrypted_files"))
				.map(x -> x.toFile())
				.filter(x -> x.getName().endsWith(".encrypted"))
				.collect(Collectors.toList());
		int count = 0;
		for(File file : filesInFolder){
			try {
				String path = "encrypted_files/";
				String path2 = "src/main/resources/decrypted_files/";
				String tmp = path2 + file.getName();
				// capire perchè non andasse
				//String decryptedFileName= tmp.split(".")[0];
				String[] tmp2 = tmp.split("\\.");
				//for(String string : tmp2)
				//	System.out.println(string);
				String decryptedFileName = tmp2[0] + "." + tmp2[1];
				//System.out.println(decryptedFileName);
				String encryptedFileName = path + file.getName();
				Resource resource = new ClassPathResource(encryptedFileName);
				File encryptedFile = resource.getFile();
				FileInputStream inputStream = new FileInputStream(encryptedFile);
				File decryptedFile = new File(decryptedFileName);
				FileOutputStream outputStream = new FileOutputStream(decryptedFile);
				decrypt(secret, inputStream, outputStream, Base64.getDecoder().decode(ivString),
						Base64.getDecoder().decode(saltString));
				inputStream.close();
				outputStream.close();
				System.out.println("file " + file.getName() + " has been decrypted!");
				count++;
			} catch (IllegalBlockSizeException | BadPaddingException | IOException | InvalidAlgorithmParameterException
				| InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException exception) {
				throw new IncorrectPasswordException();
			}
		}

		System.out.println("decryption is terminate..." + "\nstart to load elements into H2");

		//JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
		//		.toJobParameters();
		//jobLauncher.run(loadEvidenceJob, jobParameters);
	}


	public byte[] digestPassword(String secret) throws NoSuchAlgorithmException {
		byte[] secretByte = secret.getBytes(StandardCharsets.UTF_8);
		final MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(secretByte);
	}

}
