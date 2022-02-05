package com.example.uploadCSVtoH2.batch_chunk_config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.sql.DataSource;
import javax.xml.crypto.Data;


import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.read_from_filesystem.ReadFromFileSystem;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchChunkConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //@Autowired
    //private EntityManagerFactory emf;

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:batchdb");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }


    /*@Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(getDataSource());
        factory.setTransactionManager(transactionManager());
        return factory.getObject();
    }*/

    /*@Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:batchdb");
        return dataSource;
    }*/

    /*@Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }*/

    /*@Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }*/

    @Bean
    public FlatFileItemReader<Evidence> evidenceItemReader() {

        FlatFileItemReader<Evidence> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("MOCK_DATA2.csv"));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("id", "first_name", "last_name", "email", "gender", "ip_address");
        delimitedLineTokenizer.setDelimiter(",");

        //EvidenceFieldSetMapper fieldSetMapper = new EvidenceFieldSetMapper();

        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new EvidenceFieldSetMapper());

        reader.setLineMapper(defaultLineMapper);

        return reader;
    }

    @Bean
    public ItemProcessor<Evidence, Evidence> evidenceItemProcessor() {
        return new Processor();
    }

    // itemWriter con JDBC
    @Bean
    public JdbcBatchItemWriter<Evidence> evidenceItemWriter() {
        return new JdbcBatchItemWriterBuilder<Evidence>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO evidence (id, first_name, last_name, email, gender, ip_address) VALUES (:id, :first_name, :last_name, :email, :gender, :ip_address)")
                .dataSource(getDataSource())
                .build();
    }

    // itemWriter con JPA
    /*@Bean
    public JpaItemWriter<Evidence> evidenceItemWriter() {
        JpaItemWriter<Evidence> writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;
    }*/

    @Bean
    public Job loadEvidenceJob(Step step1) throws Exception {
        return jobBuilderFactory.get("loadEvidenceJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Evidence> evidenceItemWriter) {
        return stepBuilderFactory.get("step1")
                .<Evidence, Evidence>chunk(5)
                .reader(evidenceItemReader())
                .processor(evidenceItemProcessor())
                .writer(evidenceItemWriter)
                .build();
    }

}