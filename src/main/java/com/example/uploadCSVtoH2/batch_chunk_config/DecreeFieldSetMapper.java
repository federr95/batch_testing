package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.Decree;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class DecreeFieldSetMapper implements FieldSetMapper<Decree> {

    @Override
    public Decree mapFieldSet(FieldSet fieldSet) {

        final Decree decree = new Decree();
        decree.setDecreeId(Long.parseLong(fieldSet.readString("decreeId")));
        decree.setDate(fieldSet.readString("date"));
        decree.setEvidenceNumber(Integer.parseInt(fieldSet.readString("evidenceNumber")));

        return decree;
    }
}
