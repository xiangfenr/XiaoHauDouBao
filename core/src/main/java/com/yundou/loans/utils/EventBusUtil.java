package com.yundou.loans.utils;



import org.greenrobot.eventbus.EventBus;

/**
 * Created by zuohp on 2020/1/13.
 * 将EventBus封装一层.
 */
public class EventBusUtil {

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static boolean isRegistered(Object subscriber) {
        return EventBus.getDefault().isRegistered(subscriber);
    }

    public static void sendEvent(MessageEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void sendEvent(Message event) {
        EventBus.getDefault().post(event);
    }

    public static void sendEvent(MessageTEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void sendStickyEvent(MessageEvent event) {
        EventBus.getDefault().postSticky(event);
    }
}
