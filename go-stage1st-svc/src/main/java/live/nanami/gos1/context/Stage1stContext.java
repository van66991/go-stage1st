package live.nanami.gos1.context;

import live.nanami.gos1.common.MyStringUtil;
import live.nanami.gos1.common.NoLoginException;
import live.nanami.gos1.common.Pushable;
import live.nanami.gos1.config.ServerChanConfig;
import live.nanami.gos1.config.Stage1stConfig;
import live.nanami.gos1.service.Stage1stService;
import live.nanami.gos1.vo.Stage1stPost;
import lombok.Getter;
import org.apache.http.client.CookieStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 上下文(Jvm缓存集合)
 * 不想接数据库，先全接Jvm缓存这样子，后边无非是替换数据源
 *
 * @author takumi
 * @since 2024/11/27
 */
@Getter
@Component
public class Stage1stContext implements InitializingBean {

    @Autowired
    private ServerChanConfig serverChanConfig;

    @Autowired
    private Stage1stConfig s1Config;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    ApplicationContext ac;


    /**
     * key -> 网站host 例：www.saraba1st.com
     */
    private final Map<String, CookieStore> cookieStoreMap = new ConcurrentHashMap<>();

    private final Map<String, Stage1stPost> stage1stPostMap = new ConcurrentHashMap<>();

    public CookieStore selectCookieStore(URI uri){
        if (uri == null || MyStringUtil.isEmpty(uri.getHost())){
            throw new IllegalArgumentException("invalid uri");
        }
        CookieStore cookieStore = cookieStoreMap.get(uri.getHost());
        if (cookieStore == null) {
            throw new NoLoginException("no cookie record");
        }
        return cookieStore;
    }

    public CookieStore updateCookieStoreMap(URI uri, CookieStore cookieStore){
        String host = uri.getHost();
        cookieStoreMap.put(host, cookieStore);
        return cookieStore;
    }

    public CookieStore deleteCookieStore(URI uri) {
        String host = uri.getHost();
        return cookieStoreMap.remove(host);
    }

    public List<Pushable> getS1PostListToPush(){
        return stage1stPostMap.values().stream().filter(new Predicate<Stage1stPost>() {
            @Override
            public boolean test(Stage1stPost post) {
                return !post.isPushed();
            }
        }).collect(Collectors.toList());
    }

    public void cleanCachePostToThreshold(int threshold){
        List<Stage1stPost> sortedPosts = stage1stPostMap.values().stream().sorted(new Comparator<Stage1stPost>() {
            @Override
            public int compare(Stage1stPost o1, Stage1stPost o2) {
                return o1.getPostId().compareTo(o2.getPostId());
            }
        }).collect(Collectors.toList());
        int nowCnt = sortedPosts.size();
        System.out.println("now Post Count is ["+nowCnt+"] Config threshold is ["+threshold+"]");
        if (nowCnt > threshold){
            // 需要清缓存
            System.out.println("Start Clean Cache...");
            stage1stPostMap.clear();
            int startIdx = nowCnt - threshold;
            List<Stage1stPost> remained = sortedPosts.subList(startIdx, nowCnt - 1);
            for (Stage1stPost remainedPost : remained) {
                stage1stPostMap.put(remainedPost.getPostId(),remainedPost);
            }
            System.out.println("After Clean Cache Size is ["+stage1stPostMap.size()+"]");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Stage1stService stage1stService = ac.getBean(Stage1stService.class);
        stage1stService.loginStage1st();
        long fetchInterval = s1Config.getFetchInterval();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    stage1stService.heartbeat_LoginIfNot();
                    cleanCachePostToThreshold(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },30000L,30000L, TimeUnit.MILLISECONDS);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                stage1stService.getStage1stPageInfoNewest();
            }
        },31000L,fetchInterval,TimeUnit.MILLISECONDS);
    }
}
