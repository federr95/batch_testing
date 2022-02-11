package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.Evidence;
import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<Evidence, Evidence> {

    public Evidence process(Evidence evidence) throws Exception {
        /*
            code to add if we want to modify fields before insert into the table
         */
        return evidence;
    }
}