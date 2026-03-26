package com.yundou.loans.http;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ResultBean<T> implements Serializable {

    @SerializedName(value = "data")
    private T data;
    private T result;
    @SerializedName(value = "msg")
    private String msg;
    private String message;
    private String ret;
    private String traceId;
    private boolean success;
    private int code;
    private String picc_s_mid;
    //message 模块接口返回的
    private String mobile;
    private String cryptical_mobile;
    private boolean check_flag;
    private String code_id;
    //首页星级弹窗返回
    private String is_show;
    private String ecif_vip_level;
    private String host_vip_level;
    private String supple_msg;
    //个人中心用户星级返回
    private String vip_level;
    //首页改版针对缓存
    private boolean needReload;
    private String requestTime;
    private Boolean is_more;
    private Boolean lastPage;
    private int page_number;

    private int status;
    private String info;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }



    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public Boolean getIs_more() {
        return is_more;
    }

    public void setIs_more(Boolean is_more) {
        this.is_more = is_more;
    }

    //2.4签约查询接口-第三方代扣功能用于招商银行签约成功后的跳转接口
    private String redirect;
    //版本更新返回
    @SerializedName(value = "defmap")
    private T defmap;

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getVip_level() {
        return vip_level;
    }

    public void setHost_vip_level(String host_vip_level) {
        this.host_vip_level = host_vip_level;
    }

    public String getHost_vip_level() {
        return host_vip_level;
    }

    public void setVip_level(String vip_level) {
        this.vip_level = vip_level;
    }

    public void setDefmap(T defmap) {
        this.defmap = defmap;
    }

    public T getDefmap() {
        return defmap;
    }

    public String getIs_show() {
        return is_show;
    }

    public void setIs_show(String is_show) {
        this.is_show = is_show;
    }

    public String getEcif_vip_level() {
        return ecif_vip_level;
    }

    public void setEcif_vip_level(String ecif_vip_level) {
        this.ecif_vip_level = ecif_vip_level;
    }

    public String getSupple_msg() {
        return supple_msg;
    }

    public void setSupple_msg(String supple_msg) {
        this.supple_msg = supple_msg;
    }

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    public String getPicc_s_mid() {
        return picc_s_mid;
    }

    public void setPicc_s_mid(String picc_s_mid) {
        this.picc_s_mid = picc_s_mid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isCheck_flag() {
        return check_flag;
    }

    public void setCheck_flag(boolean check_flag) {
        this.check_flag = check_flag;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getsid() {
        return picc_s_mid;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getCryptical_mobile() {
        return cryptical_mobile;
    }

    public boolean isNeedReload() {
        return needReload;
    }

    public void setNeedReload(boolean needReload) {
        this.needReload = needReload;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public void setCryptical_mobile(String cryptical_mobile) {
        this.cryptical_mobile = cryptical_mobile;
    }

    public Boolean getLastPage() {
        return lastPage;
    }

    public void setLastPage(Boolean lastPage) {
        this.lastPage = lastPage;
    }
}

