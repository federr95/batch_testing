package com.example.uploadCSVtoH2.simple_read_config;

import com.example.uploadCSVtoH2.simple_read.SimpleRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class SimpleReadConfig {

    @Autowired
    public DataSource dataSource;

    @Bean
    public SimpleRead simpleRead(){
        System.out.println("creation of ReadFromFileSystem instance");
        return new SimpleRead("src/main/resources/big_test_file/fake_csv_1000000.csv");
    }

}
