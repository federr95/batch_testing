package com.example.uploadCSVtoH2.read_from_filesystem_config;

import com.example.uploadCSVtoH2.read_from_filesystem.ReadFromFileSystem;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class ReadFromFileSystemConfig {

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
    public ReadFromFileSystem readFromFileSystem(){
        System.out.println("creation of ReadFromFileSystem instance");
        return new ReadFromFileSystem("src/main/resources/MOCK_DATA2.csv");
    }

}
