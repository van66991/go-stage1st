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

    void heartbeat_LoginIfNot();

    void loginStage1st();

    SimpleRsp getStage1stPageInfo(GetStage1stPageInfoParam param);

    SimpleRsp getStage1stPageInfoNewest();

    void processParsedS1Post(Stage1stPost post);

}
