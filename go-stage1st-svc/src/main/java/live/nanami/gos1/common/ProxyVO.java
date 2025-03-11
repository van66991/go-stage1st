package live.nanami.gos1.common;

import lombok.Data;

/**
 * 代理信息
 *
 * @author takumi
 * @since 2024/11/27
 */
@Data
public class ProxyVO {
    /**
     * ip地址
     */
    private String ip;

    /**
     * 端口
     */
    private String port;

    /**
     * 状态
     * 0-活跃 1-死亡
     */
    private String status;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 所在省
     */
    private String province;

    /**
     * 所在市
     */
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
