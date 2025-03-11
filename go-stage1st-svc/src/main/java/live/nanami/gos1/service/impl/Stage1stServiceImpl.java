package live.nanami.gos1.service.impl;

import live.nanami.gos1.common.*;
import live.nanami.gos1.context.Stage1stConstant;
import live.nanami.gos1.context.Stage1stContext;
import live.nanami.gos1.service.PushService;
import live.nanami.gos1.service.Stage1stService;
import live.nanami.gos1.vo.GetStage1stPageInfoParam;
import live.nanami.gos1.vo.Stage1stPost;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * S1相关Service 实现
 *
 * @author arichi
 * @since 2024/12/12
 */
@Service
public class Stage1stServiceImpl implements Stage1stService {

    Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    Stage1stContext context;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    PushService pushService;

    @Override
    public void heartbeat() {
        try {
            logger.info("{} Heartbeat...",LOG_TAG);
            SimpleRsp rsp = getStage1stPageInfo(GetStage1stPageInfoParam.builder()
                    .needDispatch(false)
                    .buildingNum(context.getS1Config().getBuildingNum())
                    .pageNum(context.getS1Config().getPageNo())
                    .build());
            if (!rsp.isSuccess()){
                if (CodeType.NO_LOGIN.getCode() == rsp.getCode()) {
                    loginStage1st();
                    logger.info("{} Heartbeat find NOT_LOGIN, login now...",LOG_TAG);
                }
                logger.info("{} Heartbeat NOT_GOOD...",LOG_TAG);
            } else {
                logger.info("{} Heartbeat OK...",LOG_TAG);
            }
        } catch (Exception e) {
            logger.warn("{} Heartbeat NOT_GOOD...",LOG_TAG,e);
        }
    }

    @Override
    public void loginStage1st() {
        HttpClient httpClient = null;
        try {
            System.out.println("start loginStage1st...");
            // 创建HttpClient实例
            BasicCookieStore cookieStore = new BasicCookieStore();
            httpClient = HttpClientFactory.sslClient(cookieStore);

            // 创建HttpGet请求
            Map<String, String> s1LoginUrlExtraParam = buildS1LoginUrlExtraParam();
            // 获得URI
            URI targetURI = new URIBuilder(Stage1stConstant.URL_STAGE1ST_LOGIN)
                    .addParameters(MyHttpUtil.map2ListNameValuePair(s1LoginUrlExtraParam))
                    .build();
            context.updateCookieStoreMap(targetURI, cookieStore);
            HttpPost httpPost = new HttpPost(targetURI);


            // 设置请求头
            Header[] headers = HttpHeaderBuilder.builder()
                    .add(MyHttpUtil.HEADER_NAME_USER_AGENT, MyHttpUtil.USER_AGENT_INSTANCE_1)
                    .build();
            for (Header header : headers) {
                httpPost.addHeader(header);
            }

            // 设置请求体
            Map<String, String> loginFormData = buildS1LoginFormData(context.getS1Config().getUsername(), context.getS1Config().getPassword());
            HttpEntity formEntity = MyHttpUtil.map2DefaultHttpFormEntity(loginFormData);
            httpPost.setEntity(formEntity);

            // 发送请求
            HttpResponse rsp = httpClient.execute(httpPost);

            if (MyHttpUtil.HTTP_RESP_STATUS_OK == rsp.getStatusLine().getStatusCode()) {
                // 保存登录成功的CookieStore
                System.out.println("Login Success!");
                System.out.println("Cookies: " + cookieStore.toString());
            } else {
                System.out.println("Login Failed!");
                context.deleteCookieStore(targetURI);
                // todo 失败 如何处理
            }

            // 处理响应
            String rspBodyString = EntityUtils.toString(rsp.getEntity());

            System.out.println("Response Code: " + rsp.getStatusLine().getStatusCode());
            System.out.println("Response Body: " + rspBodyString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpClientHelper.closeHttpClient(httpClient);
        }
    }

    /**
     * 核中核方法
     *
     * @param param GetStage1stPageInfoParam
     * @return SimpleRsp
     */
    @Override
    public SimpleRsp getStage1stPageInfo(GetStage1stPageInfoParam param) {
        HttpClient httpClient = null;
        boolean needDispatch = param.isNeedDispatch();
        String pageNum = param.getPageNum();
        String buildingNum = param.getBuildingNum();
        try {
            logger.info("{} Start getStage1stPageInfo...",LOG_TAG);
            URI targetURI = new URIBuilder(String.format(Stage1stConstant.FMT_URL_STAGE1ST_POST_INFO,buildingNum,pageNum)).build();
            // 创建HttpClient实例 带Cookie
            CookieStore cookieStore = context.selectCookieStore(targetURI);
            httpClient = HttpClientFactory.sslClient(cookieStore);

            // 创建HttpGet请求
            HttpGet getReq = new HttpGet(targetURI);

            // 设置请求头
            Header[] headers = HttpHeaderBuilder.builder()
                    .add(MyHttpUtil.HEADER_NAME_USER_AGENT, MyHttpUtil.USER_AGENT_INSTANCE_1)
                    .build();
            for (Header header : headers) {
                getReq.addHeader(header);
            }

            // 发送请求
            HttpResponse rsp = httpClient.execute(getReq);

            // 处理响应
            String rspBodyString = EntityUtils.toString(rsp.getEntity());

            Document parsedDoc = Jsoup.parse(rspBodyString);
            logger.info("{} success parsed HTML to Document...",LOG_TAG);

            Element postlist = parsedDoc.getElementById("postlist");
            if (postlist == null) {
                throw new NoLoginException("cookie not right perhaps cookie is expired...");
            }

            Elements posts = postlist.select("#postlist > div[id^=\"post_\"]");
            if (posts.isEmpty()){
                throw new NoLoginException("cookie not right perhaps cookie is expired...");
            }

            if (needDispatch) {
                for (Element post : posts) {
                    processPostHttpElement(post);
                }
                pushService.pushAll(context.getS1PostListToPush());
            }
            // 太长了 注了
//            System.out.println("Response Code: " + rsp.getStatusLine().getStatusCode());
//            System.out.println("Response Body: " + rspBodyString);
            return SimpleRsp.success();
        } catch (NoLoginException e) {
            // 这里出错了 基本上就是没登录了
            e.printStackTrace();
            return SimpleRsp.fail(CodeType.NO_LOGIN);
        } catch (Exception e) {
            e.printStackTrace();
            return SimpleRsp.defaultFail();
        } finally {
            HttpClientHelper.closeHttpClient(httpClient);
        }
    }

    @Override
    public SimpleRsp getStage1stPageInfoByConfig() {
        GetStage1stPageInfoParam param = GetStage1stPageInfoParam.builder()
                .buildingNum(context.getS1Config().getBuildingNum())
                .pageNum(context.getS1Config().getPageNo())
                .needDispatch(true)
                .build();
        return getStage1stPageInfo(param);
    }

    @Override
    public void processS1Post(Stage1stPost parsed) {
        Map<String, Stage1stPost> stage1stPostMap = context.getStage1stPostMap();
        String postId = parsed.getPostId();
        if (stage1stPostMap.containsKey(postId)){
            // 已有记录 仅更新mdContent就行
            Stage1stPost old = stage1stPostMap.get(postId);
            String newContent = parsed.getMdContent();
            String oldContent = old.getMdContent();
            if (!oldContent.equals(newContent)){
                old.setMdContent(newContent);
            }
        } else {
            parsed.beforeFirstSave();
            stage1stPostMap.put(postId, parsed);
        }
    }

    private void processPostHttpElement(Element post) {
        try {
            String postId = post.id();
            Element user = post.getElementsByClass("authi").first();
            String userName = user.child(0).text();
            Element content = post.getElementsByClass("t_f").first();
            // 仅直属于本标签的文本内容
            String text = content.ownText();
            // 特殊处理 s1的img标签的src是假的，真正的图片地址在file 但是用的copy_down库用的src 这个用file属性如果有值 就应该覆盖掉错误的src值
            // 操 要不不推图片了 图片还有权限我是没想到的
            // 可以 不推图片体验++++
            Elements imgs = content.getElementsByTag("img");
            for (Element img : imgs) {
//                String fileUrl = img.attr("file");
//                if (MyStringUtil.isNotEmpty(fileUrl)){
//                    img.attr("src",fileUrl);
//                }
                img.remove();
            }

            String htmlContent = content.html();


            Stage1stPost stage1stPost = Stage1stPost.builder()
                    .postId(postId)
                    .username(userName)
                    .rawText(text)
                    .htmlContent(htmlContent)
                    .isDispatched(false)
                    .mdContent(MyHttpUtil.getMarkdown(htmlContent))
                    .build();
            System.out.println(stage1stPost.getClass().getSimpleName() + " ==> " + stage1stPost);
            processS1Post(stage1stPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> buildS1LoginUrlExtraParam() {
        ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
        data.put("mod", "logging");
        data.put("action", "login");
        data.put("loginsubmit", "yes");
        data.put("infloat", "yes");
        data.put("lssubmit", "yes");
        data.put("inajax", "1");
        return data;
    }

    private Map<String, String> buildS1LoginFormData(String username, String password) {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("fastloginfield", "username");
        map.put("quickforward", "yes");
        map.put("handlekey", "ls");
        map.put("username", username);
        map.put("password", password);
        return map;
    }

}
