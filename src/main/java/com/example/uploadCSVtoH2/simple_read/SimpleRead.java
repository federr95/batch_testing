/*package com.example.uploadCSVtoH2.simple_read;

import com.example.uploadCSVtoH2.entity.User;
import com.example.uploadCSVtoH2.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Data
public class SimpleRead {

    private long startTime;
    private long finishTime;
    private long elapsedTime;
    private String fileName;

    @Autowired
    DataSource dataSource;

    public SimpleRead(String fileName){
        this.fileName = fileName;
    }

    public void readFile(UserRepository userRepository) {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            int counter = userRepository.findAll().size();
            if(counter == 0)
                System.out.println("simple reading of:      -     " + fileName);
            else System.out.println("file is already read by Batch process!\n" + "starts simple read of:  -     " + fileName);

            startTime = System.currentTimeMillis();
            System.out.println("reading starts at:      -     " + startTime + " milliseconds");
            while ((line = br.readLine()) != null) {
                if (counter != userRepository.findAll().size()) {
                    String[] arrayList = line.split(",");
                    User user = new User(Integer.parseInt(arrayList[0]), arrayList[1], arrayList[2],
                            arrayList[3], arrayList[4], arrayList[5]);
                    userRepository.save(user);
                }
                counter++;
            }
            finishTime = System.currentTimeMillis();
            System.out.println("reading finish at:      -     " + finishTime + " milliseconds");
            elapsedTime = (finishTime - startTime) / 1000;
            System.out.println("read execution lasted:  -     " + elapsedTime + "seconds");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/