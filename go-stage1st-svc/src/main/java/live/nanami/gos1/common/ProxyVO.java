package live.nanami.gos1.common;

import lombok.Data;

/**
 * 代理
 *
 * @author takumi
 * @since 2024/11/27
 */
@Data
public class ProxyVO {
    private String ip;
    private String port;

    /**
     * 状态
     * 0-活跃 1-死亡
     */
    private String status;
    private String country;
    private String province;
    private String city;
    private String isp;

    private String anonymity;

    private String requestNum;

    private String source;

    private String speed;

    private String successNum;

    private String time;

    private String type;

}
