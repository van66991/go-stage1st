package live.nanami.gos1.common;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 阿帕奇HttpClient包的自用包装Util
 *
 * @author arichi
 * @since 2024/12/11
 */
public class HttpClientHelper {

    public static final String HTTPS_PATH_PREFIX = "https://";

    /**
     * 获取 HttpClient
     *
     * @param path 完整路径
     * @return {@link HttpClient}
     */
    public static HttpClient wrapClient(String path) {
        if (MyStringUtil.isNotEmpty(path) && path.startsWith(HTTPS_PATH_PREFIX)) {
            return HttpClientFactory.sslClient();
        } else {
            return HttpClientBuilder.create().build();
        }
    }

    public static HttpClient wrapClientWithProxy(String path, ProxyVO proxyParam){
        if (MyStringUtil.isNotEmpty(path) && path.startsWith(HTTPS_PATH_PREFIX)) {
            return HttpClientFactory.sslClient(proxyParam);
        } else {
            return HttpClientBuilder.create().setProxy(new HttpHost(proxyParam.getIp(), Integer.parseInt(proxyParam.getPort()))).build();
        }
    }

    public static void closeHttpClient(HttpClient httpClient) {
        if (null != httpClient) {
            try {
                if (httpClient instanceof CloseableHttpClient) {
                    CloseableHttpClient closeableHttpClient = (CloseableHttpClient) httpClient;
                    closeableHttpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
