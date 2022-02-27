package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.User;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class UserFieldSetMapper implements FieldSetMapper<User> {

    @Override
    public User mapFieldSet(FieldSet fieldSet) {

        final User user = new User();
        user.setUserId(Long.parseLong(fieldSet.readString("id")));
        user.setFirst_name(fieldSet.readString("first_name"));
        user.setLast_name(fieldSet.readString("last_name"));
        user.setEmail(fieldSet.readString("email"));
        user.setGender(fieldSet.readString("gender"));
        user.setIp_address(fieldSet.readString("ip_address"));

        return user;
    }
}



