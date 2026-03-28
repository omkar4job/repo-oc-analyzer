package com.vantage.bulls.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // This is critical: it tells Spring to look for @Async annotations
public class AsyncConfig {

    /**
     * Define the spExecutor bean.
     * We limit this to a single thread to ensure that one calculation batch
     * finishes before the next one starts, preventing database deadlocks.
     */
    @Bean(name = "spExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // CORE SETTINGS
        // setCorePoolSize(1) ensures we don't run calculations in parallel
        executor.setCorePoolSize(1); 
        executor.setMaxPoolSize(1);
        
        // QUEUE SETTINGS
        // If an SP takes 6 seconds but data comes every 4, 
        // the next task waits here in the queue.
        executor.setQueueCapacity(100); 
        
        // OPERATIONAL SETTINGS
        executor.setThreadNamePrefix("OC-Calc-Worker-");
        
        // Shutdown behavior: wait for tasks to finish when the app stops
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}