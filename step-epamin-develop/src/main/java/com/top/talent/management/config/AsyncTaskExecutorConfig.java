package com.top.talent.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync

public class AsyncTaskExecutorConfig {

    @Bean(name = "practiceDelegateEmailExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("PracticeDelegateEmailExecutor-");
        executor.initialize();
        return new DelegatingSecurityContextExecutor(executor);
    }

    @Bean("asyncSchedulerTaskExecutor")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("AsyncTaskScheduler-");
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }


    @Bean("asyncEmailNotificationExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncEmailExecutor-");
        executor.initialize();
        return new DelegatingSecurityContextExecutor(executor);
    }

}
