package live.nanami.gos1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author arichi
 * @since 2024/12/16
 */
@Configuration
@ConfigurationProperties(prefix = "stage1st")
@Data
public class Stage1stConfig {
    private String buildingNum;
    private String pageNo = "9999";
    private String username;
    private String password;
    /**
     * 抓取间隔 单位毫秒 默认3分钟
     */
    private long fetchInterval = 10000L;
    /**
     * 推送方式
     * 1 - fetch and push 跟随抓取同步推送
     * 2 - push in self thread 推送独立线程 需配合pushInterval (暂不支持！！！)
     */
    private int pushStyle = 1;
    /**
     * 推送间隔 单位毫秒 默认3分钟
     * pushStyle = 2 才生效
     */
    private long pushInterval = 180000L;
}
