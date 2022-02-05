package com.example.uploadCSVtoH2.batch_chunk_config;

import com.example.uploadCSVtoH2.UploadCsVtoH2Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BatchChunkConfig.class, UploadCsVtoH2Application.class})
public class BatchChunkConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenChunksJob_whenJobEnds_thenStatusCompleted() throws Exception {

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }


}