package com.example.uploadCSVtoH2.controller;


import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.service.EvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

@Controller
public class EvidenceController {

    @Autowired
    EvidenceService evidenceService;

    @GetMapping("/")
    public String getIndex(){
        return "index";
    }

    @GetMapping("/load-from-fileSystem")
    @ResponseBody
    public void startOperation(){
        long startTime;
        long finishTime;
        long elapsedTime;
        String file = "src/main/resources/MOCK_DATA2.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int counter = 0;
            startTime = System.currentTimeMillis();
            System.out.println("reading starts at - " + startTime + " milliseconds");
            while ((line = br.readLine()) != null) {
                if(counter != 0) {
                    String[] arrayList = line.split(",");
                    Evidence evidence = new Evidence(Integer.parseInt(arrayList[0]), arrayList[1], arrayList[2],
                            arrayList[3], arrayList[4], arrayList[5]);
                    evidenceService.addEvidence(evidence);
                }
                counter++;
            }
            finishTime = System.currentTimeMillis();
            System.out.println("reading starts at - " + finishTime + " milliseconds");
            elapsedTime = (finishTime - startTime)/1000;
            System.out.println("read execution lasted - " + elapsedTime + "seconds");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/evidences")
    @ResponseBody
    public List<Evidence> getEvidence(){
        return evidenceService.getEvidence();
    }

}
