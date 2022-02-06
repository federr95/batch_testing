package com.example.uploadCSVtoH2.simple_read_config;

import com.example.uploadCSVtoH2.simple_read.SimpleRead;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class SimpleReadConfig {

    @Bean
    public DataSource getDataSource() {
        System.out.println("creation of DataSource instance");
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:batchdb");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }

    @Bean
    public SimpleRead simpleRead(){
        System.out.println("creation of ReadFromFileSystem instance");
        return new SimpleRead("src/main/resources/MOCK_DATA2.csv");
    }

}
