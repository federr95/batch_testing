package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.entity.User;
import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<User, User> {

    public User process(User user) {
        return user;
    }
}