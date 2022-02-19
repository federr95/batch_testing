package com.example.uploadCSVtoH2.batch_chunk_config;

import javax.sql.DataSource;

import com.example.uploadCSVtoH2.entity.Evidence;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

//se metto @Configuration al posto di @Component, oltre alla creazione dei bean
//viene lanciato anche il job (il jobLauncher viene creato automaticamente)
@Configuration
//@EnableBatchProcessing
public class BatchChunkConfig {

    @Autowired
    public DataSource dataSource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

    @Bean
    public FlatFileItemReader<Evidence> evidenceItemReader() {
        System.out.println("Creation a instance of evidenceItemReader");
        FlatFileItemReader<Evidence> reader = new FlatFileItemReader<>();
        Resource resources = new ClassPathResource("fake_csv_1000000_decrypted_version.csv");
        if(!resources.isReadable() && !resources.exists())
            System.out.println("errori");
        reader.setStrict(false);
        reader.setResource(resources);

        reader.setLinesToSkip(100);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("id", "first_name", "last_name", "email", "gender", "ip_address");
        delimitedLineTokenizer.setDelimiter(",");

        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new EvidenceFieldSetMapper());

        reader.setLineMapper(defaultLineMapper);

        return reader;
    }

    @Bean
    public ItemProcessor<Evidence, Evidence> evidenceItemProcessor() {
        System.out.println("Creation a instance of evidenceItemProcessor");
        return new Processor();
    }

    @Bean
    public JdbcBatchItemWriter<Evidence> evidenceItemWriter() {
        System.out.println("Creation a instance of evidenceItemWriter");
        return new JdbcBatchItemWriterBuilder<Evidence>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO evidence (id, first_name, last_name, email, gender, ip_address) VALUES (:id, :first_name, :last_name, :email, :gender, :ip_address)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job loadEvidenceJob() {
        System.out.println("Creation a instance of loadEvidenceJob");
        return jobBuilderFactory
                .get("loadEvidenceJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        System.out.println("Creation a instance of step1");
        return stepBuilderFactory.get("step1")
                .<Evidence, Evidence>chunk(100)
                .reader(evidenceItemReader())
                .processor(evidenceItemProcessor())
                .writer(evidenceItemWriter())
                .build();
    }

}