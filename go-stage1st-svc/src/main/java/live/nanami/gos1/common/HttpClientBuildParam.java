package live.nanami.gos1.common;

import lombok.Data;
import org.apache.http.client.CookieStore;

/**
 * 先把能想到的加上
 *
 * @author arichi
 * @since 2024/12/11
 */
@Data
public class HttpClientBuildParam {
    private ProxyVO proxyParam;
    private CookieStore cookieStore;
}
