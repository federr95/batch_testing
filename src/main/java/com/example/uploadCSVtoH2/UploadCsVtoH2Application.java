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

	// ENCRYPTION (all file inside resources' folder)
	/*@Bean
	public void encryptionFolder() throws NoSuchAlgorithmException, IOException {
		String secret = "PASSWORD";
		List<File> filesInFolder = Files.walk(Paths.get("C:/Users/feder/Downloads/batch_testing/src/main/resources"))
				.filter(x -> Files.isRegularFile(x.getFileName()))
				.map(x -> x.toFile())
				.collect(Collectors.toList());
		for(File file : filesInFolder){
			System.out.println(file.getName());
		}
		long fileCheck = filesInFolder.stream()
				.filter(x -> x.getName().equals("fake_csv_1000000.encrypted"))
				.count();
		if(fileCheck == 0){
			Resource resource = new ClassPathResource("fake_csv_1000000.csv");
			File inputFile = resource.getFile();
			String fileName = "src/main/resources/fake_csv_1000000.encrypted";
			File encryptedFile = new File(fileName);
			FileInputStream inputStream = new FileInputStream(inputFile);
			FileOutputStream outputStream = new FileOutputStream(encryptedFile);
			PasswordAESCryptoUtils passAESCryptoUtils = new PasswordAESCryptoUtils(secret);
			encrypt("PASSWORD", inputStream, outputStream, passAESCryptoUtils.getIV(), passAESCryptoUtils.getSALT());
			/*encrypt("PASSWORD", inputStream, outputStream, passAESCryptoUtils.getIvString().getBytes(StandardCharsets.US_ASCII),
						passAESCryptoUtils.getSaltString().getBytes(StandardCharsets.US_ASCII));*/
		/*} else {
			System.out.println("File already encrypted");
		}
	}*/

	// DECRYPTION
	@Bean
	public void decryptionFolder() throws NoSuchAlgorithmException, InvalidKeySpecException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, IOException {

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

		List<File> filesInFolder = Files.walk(Paths.get("C:/Users/feder/Downloads/batch_testing/src/main/resources/encrypted_files"))
				.map(x -> x.toFile())
				.filter(x -> x.getName().endsWith(".encrypted"))
				.collect(Collectors.toList());
		int count = 0;
		for(File file : filesInFolder){
			try {
				String path = "encrypted_files/";
				String path2 = "src/main/resources/decrypted_files/";
				String decryptedFileName = path2 + "evidenceDecrypted" + count + ".csv";
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
				System.out.println("file evidence" + count + " is decrypted!");
				count++;
			} catch (IllegalBlockSizeException | BadPaddingException | IOException | InvalidAlgorithmParameterException
				| InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException exception) {
				throw new IncorrectPasswordException();
			}
		}

		System.out.println("decryption is terminate..." + "\nstart to load elements into H2");

		/*JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(loadEvidenceJob, jobParameters);*/
	}


	public byte[] digestPassword(String secret) throws NoSuchAlgorithmException {
		byte[] secretByte = secret.getBytes(StandardCharsets.UTF_8);
		final MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(secretByte);
	}

}
