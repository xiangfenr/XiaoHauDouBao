package com.yundou.loans.utils;

import androidx.annotation.Keep;

/**
 *  * @author zuohp
 *   EventBus 事件类
 */
@Keep
public class MessageTEvent<T> {

    private int code;
    private T data;

    private String message;
    public MessageTEvent() {
    }
    public MessageTEvent(int code) {
        this.code = code;
    }

    public MessageTEvent(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public MessageTEvent(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
