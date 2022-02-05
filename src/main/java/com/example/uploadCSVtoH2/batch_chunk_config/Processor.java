package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.Evidence;
import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<Evidence, Evidence> {

    public Evidence process(Evidence evidence) throws Exception {
        //evidence.setField1(evidence.getField1().toUpperCase());
        //evidence.setField2(evidence.getField2().toUpperCase());
        //System.out.println(evidence.getId());
        return evidence;
    }
}