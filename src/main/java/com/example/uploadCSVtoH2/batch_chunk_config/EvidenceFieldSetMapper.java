package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.Evidence;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class EvidenceFieldSetMapper implements FieldSetMapper<Evidence> {

    @Override
    public Evidence mapFieldSet(FieldSet fieldSet) {

        final Evidence evidence = new Evidence();
        evidence.setId(Long.parseLong(fieldSet.readString("id")));
        evidence.setFirst_name(fieldSet.readString("first_name"));
        evidence.setLast_name(fieldSet.readString("last_name"));
        evidence.setEmail(fieldSet.readString("email"));
        evidence.setGender(fieldSet.readString("gender"));
        evidence.setIp_address(fieldSet.readString("ip_address"));

        return evidence;
    }
}



