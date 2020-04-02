package com.foxconn.iisd.rcadsvc.msg;

public class RestReturnMsg {
    private Integer code;
    private String message;
    private Object data;

    public RestReturnMsg(Integer code, String message) {
        this(code, null, message);
    }

    public RestReturnMsg(Integer code, String message, Object data) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
