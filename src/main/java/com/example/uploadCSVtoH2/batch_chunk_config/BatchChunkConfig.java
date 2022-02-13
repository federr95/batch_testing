package com.example.uploadCSVtoH2.batch_chunk_config;

import javax.sql.DataSource;

import com.example.uploadCSVtoH2.entity.Evidence;
import com.example.uploadCSVtoH2.entity.EvidenceEncrypted;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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

import java.util.function.Function;

@Configuration
@EnableBatchProcessing
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

        FlatFileItemReader<Evidence> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("MOCK_DATA2.csv"));
        reader.setLinesToSkip(1);

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
    public ItemProcessor<Evidence, EvidenceEncrypted> evidenceItemProcessor() {
        return new Processor();
    }

    @Bean
    public JdbcBatchItemWriter<EvidenceEncrypted> evidenceItemWriter() {
        return new JdbcBatchItemWriterBuilder<EvidenceEncrypted>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO evidence (id, first_name, last_name, email, gender, ip_address) VALUES (:id, :first_name, :last_name, :email, :gender, :ip_address)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job loadEvidenceJob(Step step1) {
        return jobBuilderFactory.get("loadEvidenceJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<EvidenceEncrypted> evidenceItemWriter) {
        return stepBuilderFactory.get("step1")
                .<Evidence, EvidenceEncrypted>chunk(5)
                .reader(evidenceItemReader())
                .processor(evidenceItemProcessor())
                .writer(evidenceItemWriter)
                .build();
    }

}