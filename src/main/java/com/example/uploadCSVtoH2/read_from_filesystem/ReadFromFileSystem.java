package com.example.uploadCSVtoH2.read_from_filesystem;

import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.repository.EvidenceRepository;
import com.example.uploadCSVtoH2.service.EvidenceService;
import lombok.Data;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class ReadFromFileSystem {

    private long startTime;
    private long finishTime;
    private long elapsedTime;
    private String fileName;

    @Autowired
    DataSource dataSource;

    public ReadFromFileSystem(String fileName){
        this.fileName = fileName;
    }

    public void readFile(EvidenceRepository evidenceRepository) {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int counter = 0;
            startTime = System.currentTimeMillis();
            System.out.println("reading starts at - " + startTime + " milliseconds");
            while ((line = br.readLine()) != null) {
                if (counter != 0) {
                    String[] arrayList = line.split(",");
                    Evidence evidence = new Evidence(Integer.parseInt(arrayList[0]), arrayList[1], arrayList[2],
                            arrayList[3], arrayList[4], arrayList[5]);
                    //System.out.println("Evidence parameters: " + evidence.first_name + " " + evidence.last_name);
                    evidenceRepository.save(evidence);
                }
                counter++;
            }
            //System.out.println("Evidence read: " + counter);
            finishTime = System.currentTimeMillis();
            System.out.println("reading starts at - " + finishTime + " milliseconds");
            elapsedTime = (finishTime - startTime) / 1000;
            System.out.println("read execution lasted - " + elapsedTime + "seconds");
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
