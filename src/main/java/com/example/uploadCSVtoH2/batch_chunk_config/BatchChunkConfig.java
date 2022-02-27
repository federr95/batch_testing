package com.example.uploadCSVtoH2.batch_chunk_config;

import javax.sql.DataSource;

import com.example.uploadCSVtoH2.entity.User;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

//se metto @Configuration al posto di @Component, oltre alla creazione dei bean
//viene lanciato anche il job (il jobLauncher viene creato automaticamente)
@Configuration
//@EnableBatchProcessing
public class BatchChunkConfig {

    private static final String OVERRIDDEN_BY_EXPRESSION = null;

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
    @StepScope
    public FlatFileItemReader<User> itemReader(
            @Value("#{jobParameters[fileName]}") String fileName) {
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        String path = "decrypted_files/";
        Resource resources = new ClassPathResource(path + fileName);
        if(!resources.isReadable() && !resources.exists())
            System.out.println("errori");
        reader.setStrict(false);
        reader.setResource(resources);

        reader.setLinesToSkip(1);

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        switch (fileName){
            case "decreti.csv":
                delimitedLineTokenizer.setNames("decreeId", "date", "evidenceNumber", "user");
                break;
            case "messaggi.csv":
                delimitedLineTokenizer.setNames("messageId", "text", "sender", "receiver", "position");
                break;
            case "posizioni.csv":
                delimitedLineTokenizer.setNames("positionId", "latitude", "longitude", "messages", "phoneCalls");
                break;
            case "telefonate.csv":
                delimitedLineTokenizer.setNames("phoneCallId", "phoneCallReceiver", "phoneCallSender", "duration", "position");
                break;
            case "video.csv":
                delimitedLineTokenizer.setNames("videoId", "duration", "position", "format");
                break;
            case "user.csv":
                delimitedLineTokenizer.setNames("userId", "first_name", "last_name", "email", "gender", "ip_address");
                break;
        }
        delimitedLineTokenizer.setDelimiter(",");

        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new UserFieldSetMapper());

        reader.setLineMapper(defaultLineMapper);

        return reader;
    }

    @Bean
    public ItemProcessor<User, User> itemProcessor() {
        return new Processor();
    }

    @Bean
    public JdbcBatchItemWriter<User> itemWriter() {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO evidence (id, first_name, last_name, email, gender, ip_address) VALUES (:id, :first_name, :last_name, :email, :gender, :ip_address)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory
                .get("job")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(100)
                .reader(itemReader(OVERRIDDEN_BY_EXPRESSION))
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

}