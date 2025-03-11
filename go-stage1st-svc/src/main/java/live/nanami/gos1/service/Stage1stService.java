package live.nanami.gos1.service;


import live.nanami.gos1.common.SimpleRsp;
import live.nanami.gos1.vo.GetStage1stPageInfoParam;
import live.nanami.gos1.vo.Stage1stPost;

/**
 * S1相关Service
 *
 * @author arichi
 * @since 2024/12/12
 */
public interface Stage1stService {

    String LOG_TAG = "[S1]";

    /**
     * 发送心跳
     */
    void heartbeat();

    /**
     * 登录S1
     */
    void loginStage1st();

    /**
     * 获得某一版面（某楼某页）的信息
     *
     * @param param 参数集
     * @return 成功与否
     */
    SimpleRsp getStage1stPageInfo(GetStage1stPageInfoParam param);

    /**
     * 用配置中的楼号和页数获得信息
     *
     * @return 成功与否
     */
    SimpleRsp getStage1stPageInfoByConfig();

    /**
     * 处理S1帖子
     *
     * @param post
     */
    void processS1Post(Stage1stPost post);

}
