package live.nanami.gos1.service;

import live.nanami.gos1.common.Pushable;

import java.util.List;

public interface PushService {

    Integer SUCCESS = 0;
    Integer FAILED = 1;
    Integer NO_MSG_TO_SEND = 2;


    /**
     * 推送
     *
     * @param msg 要推送的消息
     * @return 返回码 0-成功 1-失败
     */
    int push(String msg,String title,String brief);

    /**
     * 推送
     *
     * @param msg 要推送的消息
     * @return 返回码 0-成功 1-失败
     */
    int push(Pushable msg);

    /**
     * 多条信息一次推送
     *
     * @param msgPackage
     * @return
     */
    int push(List<Pushable> msgPackage);

    int push(List<Pushable> msgPackage, Integer threshold);

}