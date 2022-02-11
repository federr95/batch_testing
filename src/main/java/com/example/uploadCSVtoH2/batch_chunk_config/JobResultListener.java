package com.example.uploadCSVtoH2.batch_chunk_config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@NoArgsConstructor
@Data
public class JobResultListener implements JobExecutionListener {

    private long startTime;
    private long finishTime;
    private long effectiveTime;

    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        System.out.println("job started at      - " + startTime + " milliseconds");
    }

    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED ) {
            finishTime = System.currentTimeMillis();
            effectiveTime = (finishTime - startTime)/1000;
            System.out.println("job finish at       - " + finishTime + " milliseconds");
            System.out.println("job execution time  - " + effectiveTime + " seconds");
            //job success
        }
        else {
            jobExecution.getStatus();//job failure
        }
    }
}

