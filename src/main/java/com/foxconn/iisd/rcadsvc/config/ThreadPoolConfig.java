package com.foxconn.iisd.rcadsvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${rca.threadPool.size}")
    private Integer threadPoolSize;

    @Bean
    public ExecutorService getThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
