package live.nanami.gos1.common;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Header} 建造者
 *
 * @author takumi
 * @since 2024/10/25
 */
public class HttpHeaderBuilder {

    private final Map<String, String> header = new HashMap<>();

    public static HttpHeaderBuilder builder(){
        return new HttpHeaderBuilder();
    }

    public HttpHeaderBuilder add(String name, String value) {
        this.header.put(name, value);
        return this;
    }

    public HttpHeaderBuilder addAll(Header[] headers) {
        for (Header h : headers) {
            this.header.put(h.getName(), h.getValue());
        }
        return this;
    }

    public Header[] build() {
        List<Header> list = new ArrayList<>();
        for (String key : this.header.keySet()) {
            list.add(new BasicHeader(key, this.header.get(key)));
        }
        return list.toArray(new Header[0]);
    }

}
