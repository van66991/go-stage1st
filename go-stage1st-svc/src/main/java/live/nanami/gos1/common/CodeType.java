package live.nanami.gos1.common;

/**
 * 状态返回码
 *
 * @author takumi
 * @since 2024/12/11
 */
public enum CodeType {

    /**
     * 状态码
     */
    SUCCESS(200, "成功"),
    NO_LOGIN(9002,"未登录"),
    DEFAULT_FAIL(9001,"服务端未知异常，联系开发者查看后台日志。"),
    ;

    private final int code;

    private final String message;

    CodeType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
