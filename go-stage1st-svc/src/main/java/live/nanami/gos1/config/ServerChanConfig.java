package live.nanami.gos1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author arichi
 * @since 2024/12/16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "serverchan")
public class ServerChanConfig {
    private String sendKey;
    private String uid;
    private String apiUrlFmt;
}
