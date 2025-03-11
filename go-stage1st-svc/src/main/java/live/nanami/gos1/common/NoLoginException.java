package live.nanami.gos1.common;

/**
 * 未登录导致的Ex
 *
 * @author arichi
 * @since 2024/12/17
 */
public class NoLoginException extends RuntimeException {

    private static final long serialVersionUID = -5683500378033625723L;

    public NoLoginException() {
    }

    public NoLoginException(String message) {
        super(message);
    }

    public NoLoginException(Throwable cause) {
        super(cause);
    }

    public NoLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
