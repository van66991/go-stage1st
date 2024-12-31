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
        },fetchInterval,fetchInterval,TimeUnit.MILLISECONDS);
    }
}
