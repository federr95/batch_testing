package com.example.uploadCSVtoH2.batch_chunk_config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Data
public class JobResultListener implements JobExecutionListener {

    public void beforeJob(JobExecution jobExecution) {
        System.out.println("job starts at         - " + jobExecution.getStartTime().getTime() + " milliseconds");
    }

    public void afterJob(JobExecution jobExecution) {

        boolean completed = jobExecution.getStatus() == BatchStatus.COMPLETED;
        if (completed) {
          long jobDuration = (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime())/1000;
          System.out.println("job finish at         - " + jobExecution.getEndTime().getTime() + " milliseconds");
          System.out.println("job execution time    - " + jobDuration + " seconds");
        } else {
            jobExecution.getStatus();//job failure
        }
    }
}

