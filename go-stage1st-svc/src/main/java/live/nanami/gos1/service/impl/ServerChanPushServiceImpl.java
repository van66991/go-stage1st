package live.nanami.gos1.service.impl;


import live.nanami.gos1.common.HttpClientFactory;
import live.nanami.gos1.common.MyHttpUtil;
import live.nanami.gos1.common.Pushable;
import live.nanami.gos1.config.ServerChanConfig;
import live.nanami.gos1.context.Stage1stContext;
import live.nanami.gos1.service.PushService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author arichi
 * @since 2024/12/16
 */
@Service
public class ServerChanPushServiceImpl implements PushService {

    @Autowired
    Stage1stContext context;

    @Override
    public int push(String msg,String title,String brief) {
        try {
            ServerChanConfig serverChanConfig = context.getServerChanConfig();
            String sendKey = serverChanConfig.getSendKey();
            String uid = serverChanConfig.getUid();
            String apiUrlFmt = serverChanConfig.getApiUrlFmt();
            String url = String.format(apiUrlFmt, uid, sendKey);

            // 创建HttpClient实例
            HttpClient httpClient = HttpClientFactory.sslClient();

            // 创建HttpGet/HttpPost请求
            // 获取URI
            Map<String,String> paramMap = buildPushParamMap(msg,title,brief);
            URI targetUri = new URIBuilder(url).addParameters(MyHttpUtil.map2ListNameValuePair(paramMap)).build();
            HttpGet httpGet = new HttpGet(targetUri);

            // 设置请求头
            MyHttpUtil.setDefaultHeader(httpGet);

            // 设置请求体
            // 无请求体

            // 发送请求
            HttpResponse rsp = httpClient.execute(httpGet);

            // 处理响应
            String rspBodyString = EntityUtils.toString(rsp.getEntity());
            System.out.println("Response Code: " + rsp.getStatusLine().getStatusCode());
            System.out.println("Response Body: " + rspBodyString);
        } catch (Exception e) {
            e.printStackTrace();
            return PushService.FAILED;
        }
        return PushService.SUCCESS;
    }

    private Map<String, String> buildPushParamMap(String msg, String title, String brief) {
        ConcurrentHashMap<String, String> resultMap = new ConcurrentHashMap<>();
        resultMap.put("title",title);
        resultMap.put("desp",msg);
        resultMap.put("short",brief);
        return resultMap;
    }

    @Override
    public int push(Pushable msg) {
        return push(msg.getPushContent(),msg.getTitle(),msg.getShort());
    }

    @Override
    public int push(List<Pushable> msgPackage, Integer threshold) {
        if (threshold == null || threshold < 1){
            throw new IllegalArgumentException("push threshold illegal >> "+threshold);
        }
        if (CollectionUtils.isEmpty(msgPackage)){
            return PushService.NO_MSG_TO_SEND;
        }
        List<Pushable> msgsToPush = msgPackage.stream().filter(pushable -> !pushable.isPushed()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(msgsToPush)){
            return PushService.NO_MSG_TO_SEND;
        }
        String title = msgsToPush.get(0).getTitle();
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(0);
        List<Pushable> thisTimePushedMsgList = new ArrayList<>();
        for (Pushable pushable : msgsToPush) {
            int used = count.addAndGet(1);
            if (used > 1){
                stringBuilder.append("\n\n-------------------\n\n");
            }
            stringBuilder.append(pushable.getPushContent());
            thisTimePushedMsgList.add(pushable);
            if (used >= threshold) {
                break;
            }
        }
        String content = stringBuilder.toString();

        int pushRes = push(content, title, content.substring(0, 25));
        if (PushService.SUCCESS == pushRes) {
            for (Pushable push : thisTimePushedMsgList) {
                push.pushed();
            }
        }
        return pushRes;
    }

    @Override
    public int push(List<Pushable> list){
        return push(list,5);
    }
}
