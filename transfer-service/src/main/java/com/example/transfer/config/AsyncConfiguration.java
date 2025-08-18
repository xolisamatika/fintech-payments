package com.example.transfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfiguration {
    public static final String JOB_ASYNC_EXECUTOR = "jobAsyncExecutor";
    @Value("${batch.core-pool-size}")
    private int corePoolSize;
    @Value("${batch.max-pool-size}")
    private int maxPoolSize;
    @Value("${batch.queue-capacity}")
    private int queueCapacity;

    @Bean(name = JOB_ASYNC_EXECUTOR)
    public Executor jobAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("JobAsync-");
        executor.initialize();
        return executor;
    }
}