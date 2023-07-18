package com.floney.floney.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private static final String SCHEDULE_BY_MONTH = "0 0 0 1 * *";
    private final JobLauncher jobLauncher;
    private final JobConfiguration jobConfiguration;

    @Scheduled(cron = SCHEDULE_BY_MONTH)
    public void runExampleJob() {
        Map<String, JobParameter> confMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        confMap.put("time", new JobParameter(date));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(jobConfiguration.jpaPagingItemReaderJob(), jobParameters);
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(String.format("ERRER TIME : %s", sdf.format(date)));
            ;
        }
    }
}
