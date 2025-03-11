package live.nanami.gos1.common;

/**
 * 实现此接口可使用推送服务
 *
 * @author arichi
 * @since 2024/12/17
 */
public interface Pushable {

    /**
     * 获取标题
     *
     * @return 标题
     */
    String getTitle();

    /**
     * 获取概要
     *
     * @return 概要
     */
    String getShort();

    /**
     * 推送的主内容
     *
     * @return 推送的主内容
     */
    String getPushContent();

    /**
     * 是否已经推送过
     *
     * @return boolean
     */
    boolean isPushed();

    /**
     * 推送后做一些事
     */
    void pushed();

}
