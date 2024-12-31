package live.nanami.gos1.vo;

import live.nanami.gos1.common.Pushable;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * S1一楼的帖子
 *
 * @author arichi
 * @since 2024/12/16
 */
@Data
@Builder
public class Stage1stPost implements Pushable {

    private String postId;
    private String username;
    private String rawText;
    private String htmlContent;
    private String mdContent;
    private Boolean isDispatched;

    public void beforeFirstSave(){
        this.setIsDispatched(false);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("postId", postId)
                .append("username", username)
                .append("rawText", rawText)
                .append("htmlContent", htmlContent)
                .append("mdContent", mdContent)
                .append("isDispatched", isDispatched ? "pushed":"not Pushed Yet")
                .toString();
    }

    @Override
    public String getTitle() {
        return "B综消息更新了！！";
    }

    @Override
    public String getShort() {
        return getPushContent().substring(0,10);
    }

    @Override
    public String getPushContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(username);
        sb.append("]").append("发了： ");
        sb.append(mdContent);
        return sb.toString();
    }

    @Override
    public boolean isPushed() {
        return isDispatched;
    }

    @Override
    public void pushed() {
        setIsDispatched(true);
    }
}
