package com.yundou.loans.utils;

import androidx.annotation.Keep;

/**
 *  * @author zuohp
 *   EventBus 事件类
 */
@Keep
public class MessageEvent {

    private int code;
    private MapBean data;

    private String message;

    public MessageEvent(int code) {
        this.code = code;
    }

    public MessageEvent(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public MessageEvent(int code, MapBean data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MapBean getData() {
        return data;
    }

    public void setData(MapBean data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
