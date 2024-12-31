package live.nanami.gos1.common;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * @author takumi
 * @since 2024/11/28
 */
public class HttpClientFactory {

    public static HttpClient sslClient(HttpClientBuildParam param){
        try {
            // 在调用SSL之前需要重写验证方法，取消检测SSL
            X509TrustManager trustManager = defaultX509TrustManager();

            // 创建一个自定义的X509HostnameVerifier，接受任何主机名
            X509HostnameVerifier x509HostnameVerifier = defaultX509HostnameVerifier();

            // sslContext SSL上下文 使用TLS
            SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            // Socket工厂
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, x509HostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", socketFactory)
                    .build();

            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);


            ProxyVO proxyParam = param.getProxyParam();
            HttpHost proxy = null;
            if (proxyParam!=null){
                // 设置代理
                String proxyIp = proxyParam.getIp();
                String proxyPort = proxyParam.getPort();
                proxy = new HttpHost(proxyIp,Integer.parseInt(proxyPort));
            }

            // 创建默认请求配置RequestConfig
            RequestConfig requestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(Boolean.TRUE)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .setProxy(proxy)
                    .build();

            return HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultCookieStore(param.getCookieStore())
                    .build();
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static HttpClient sslClient(ProxyVO proxyParam){
        HttpClientBuildParam httpClientBuildParam = new HttpClientBuildParam();
        httpClientBuildParam.setProxyParam(proxyParam);
        return sslClient(httpClientBuildParam);
    }

    /**
     * 在调用SSL之前需要重写验证方法，取消检测SSL
     * 创建ConnectionManager，添加Connection配置信息
     *
     * @return HttpClient 支持https
     */
    public static HttpClient sslClient() {
        HttpClientBuildParam httpClientBuildParam = new HttpClientBuildParam();
        return sslClient(httpClientBuildParam);
    }

    public static HttpClient sslClient(CookieStore cookieStore) {
        HttpClientBuildParam httpClientBuildParam = new HttpClientBuildParam();
        httpClientBuildParam.setCookieStore(cookieStore);
        return sslClient(httpClientBuildParam);
    }

    /**
     * 取消检测SSL的trustManager
     * @return X509TrustManager
     */
    private static X509TrustManager defaultX509TrustManager() {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // 不做验证
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String str) {
                // 不做验证
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String str) {
                // 不做验证
            }
        };
        return trustManager;
    }

    /**
     * 创建一个自定义的X509HostnameVerifier，接受任何主机名
     *
     * @return X509HostnameVerifier
     */
    private static X509HostnameVerifier defaultX509HostnameVerifier() {
        return new X509HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                // 接受任何主机名
                return true;
            }

            @Override
            public void verify(String s, SSLSocket sslSocket) {
                // 不做验证
            }

            @Override
            public void verify(String s, X509Certificate x509Certificate) {
                // 不做验证
            }

            @Override
            public void verify(String s, String[] strings, String[] strings1) {
                // 不做验证
            }
        };
    }
}
