package com.juran.quote.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * author:chenpeng01
 * description: 报价系统线程池配置
 */
@EnableAsync
@Configuration
@Component
public class QuoteTaskExecutor {

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolConfig.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolConfig.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolConfig.getKeepAliveSeconds());
        executor.setThreadNamePrefix(threadPoolConfig.getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(threadPoolConfig.getWaitTaskCompleteOnShutdown());
        return executor;
    }
}
