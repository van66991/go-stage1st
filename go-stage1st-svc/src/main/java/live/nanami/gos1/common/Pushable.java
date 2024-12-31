package live.nanami.gos1.common;

/**
 * @author arichi
 * @since 2024/12/17
 */
public interface Pushable {

    String getTitle();

    String getShort();

    String getPushContent();

    boolean isPushed();

    void pushed();

}
