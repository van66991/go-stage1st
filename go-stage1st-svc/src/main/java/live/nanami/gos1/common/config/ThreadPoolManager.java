package live.nanami.gos1.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池统一管理
 *
 * @author takumi
 * @since 2024/10/28
 */
@Configuration
public class ThreadPoolManager {

    /**
     * spring优化版线程池
     * @return ThreadPoolTaskExecutor
     */
    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        // todo 暂时写死 后面提取到配置
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(16);
        threadPoolTaskExecutor.setQueueCapacity(500);
        threadPoolTaskExecutor.setThreadNamePrefix("t_");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return threadPoolTaskExecutor;
    }

    /**
     * JDK定时线程池
     * @return ScheduledThreadPoolExecutor
     */
    @Bean("scheduledExecutorService")
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
