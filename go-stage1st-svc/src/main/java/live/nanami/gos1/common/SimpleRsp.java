package live.nanami.gos1.common;

import lombok.Data;

/**
 * 返回的简单包装
 *
 * @author arichi
 */
public class SimpleRsp {

    private final boolean success;
    private final int code;
    private final String msg;
    private final Object data;

    public static SimpleRsp success(){
        return success(new EmptySuccessData());
    }

    public static SimpleRsp success(Object data) {
        return new SimpleRsp(true, CodeType.SUCCESS.getCode(), CodeType.SUCCESS.getMessage(), data);
    }

    public static SimpleRsp fail(CodeType codeType){
        return fail(codeType.getCode(),codeType.getMessage());
    }

    public static SimpleRsp fail(int code, String msg) {
        return new SimpleRsp(false, code, msg, null);
    }


    public static SimpleRsp defaultFail(){
        return fail(CodeType.DEFAULT_FAIL);
    }

    public SimpleRsp(boolean success, int code, String msg, Object data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    @Data
    public static class EmptySuccessData {
        private String data = "success";
    }

}
