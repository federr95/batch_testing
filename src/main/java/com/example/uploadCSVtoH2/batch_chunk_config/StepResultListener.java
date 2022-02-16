package com.example.uploadCSVtoH2.batch_chunk_config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.annotation.Bean;

@Data
@NoArgsConstructor
public class StepResultListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //System.out.println("Called beforeStep().");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        //System.out.println("Called afterStep().");
        System.out.println("step read           - " + stepExecution.getReadCount() + " line");
        return stepExecution.getExitStatus();
    }
}
