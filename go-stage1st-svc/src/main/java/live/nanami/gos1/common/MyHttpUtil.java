package live.nanami.gos1.common;

import io.github.furstenheim.CopyDown;
import io.github.furstenheim.Options;
import io.github.furstenheim.OptionsBuilder;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author arichi
 * @since 2024/12/16
 */
public class MyHttpUtil {

    public static final int HTTP_RESP_STATUS_OK = 200;
    public static final String HEADER_NAME_USER_AGENT = "user-agent";
    public static final String USER_AGENT_INSTANCE_1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36";

    public static HttpEntity map2DefaultHttpFormEntity(Map<String,String> map) throws UnsupportedEncodingException {
        List<NameValuePair> parameters = map2ListNameValuePair(map);
        return new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
    }

    public static List<NameValuePair> map2ListNameValuePair(Map<String,String> map){
        List<NameValuePair> parameters = new ArrayList<>();
        for (Map.Entry<String, String> keyValue : map.entrySet()) {
            String key = keyValue.getKey();
            String value = keyValue.getValue();
            parameters.add(new BasicNameValuePair(key,value));
        }
        return parameters;
    }

    public static void setDefaultHeader(HttpGet httpGet) {
        Header[] headers = HttpHeaderBuilder.builder()
                .add(HEADER_NAME_USER_AGENT, USER_AGENT_INSTANCE_1)
                .build();
        for (Header header : headers) {
            httpGet.addHeader(header);
        }
    }

    public static String getMarkdown(String html) {
        OptionsBuilder optionsBuilder = OptionsBuilder.anOptions();
        Options options = optionsBuilder
                // more options
                .build();
        CopyDown converter = new CopyDown(options);
        return converter.convert(html);
    }
}
