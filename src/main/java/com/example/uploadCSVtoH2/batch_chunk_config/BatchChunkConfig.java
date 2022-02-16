package com.example.uploadCSVtoH2.batch_chunk_config;

import javax.sql.DataSource;

import com.example.uploadCSVtoH2.charset.CheckCharset;
import com.example.uploadCSVtoH2.entity.Evidence;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
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

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

@Configuration
@EnableBatchProcessing
public class BatchChunkConfig {

    @Autowired
    public CheckCharset checkCharset;

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

        ClassPathResource classPathResource = new ClassPathResource("/big_test_file/fake_csv_1000000.csv");

        try{
            //ClassPathResource classPathResourceAbsolute = new ClassPathResource("/home/federico/Downloads/uploadCSVtoH2/src/main/resources/" + classPathResource.getPath());
            //checkCharset.getCheckCharset(classPathResourceAbsolute.getPath());
            checkCharset.getCheckCharset("/home/federico/Downloads/uploadCSVtoH2/src/main/resources/big_test_file/fake_csv_1000000.csv");
            //checkCharset.getCheckCharset("/home/federico/Downloads/uploadCSVtoH2/src/main/resources/big_test_file/fake_csv_1000000(wrong_charset).csv");
        } catch (UnsupportedCharsetException | IOException exception){
            throw new RuntimeException();
        }

        FlatFileItemReader<Evidence> reader = new FlatFileItemReader<>();
        reader.setResource(classPathResource);
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
    public ItemProcessor<Evidence, Evidence> evidenceItemProcessor() {
        return new Processor();
    }

    @Bean
    public JdbcBatchItemWriter<Evidence> evidenceItemWriter() {
        return new JdbcBatchItemWriterBuilder<Evidence>()
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
    public Step step1(JdbcBatchItemWriter<Evidence> evidenceItemWriter) {
        return stepBuilderFactory.get("step1")
                .<Evidence, Evidence>chunk(100)
                .reader(evidenceItemReader())
                .processor(evidenceItemProcessor())
                .writer(evidenceItemWriter)
                .listener(new StepResultListener())
                .build();
    }

}
