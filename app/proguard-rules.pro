# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5       # 指定代码的压缩级别
-dontusemixedcaseclassnames     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses        # 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers       # 指定不去忽略包可见的库类的成员
-dontpreverify      # 混淆时是否做预校验
-verbose        # 混淆时是否记录日志
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*      # 混淆时所采用的算法
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------
-ignorewarnings     # 是否忽略检测，（是）
#---------------------------------默认保留区---------------------------------
#noinspection ShrinkerUnresolvedReference
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keep class android.support.annotation.Keep
-keep class androidx.annotation.Keep
#保留@Keep注解的类，保留
-keep @android.support.annotation.Keep class * {*;}
-keep @androidx.annotation.Keep class * {*;}
#保留@Keep修饰的方法
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
#保留@Keep修饰的字段
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
#保留@Keep修饰的构造方法
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
#-ignorewarnings -keep class * { public private *; }

#如果有引用v4包可以添加下面这行
#-keep class android.support.v4.** { *; }
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.app.Fragment

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-dontwarn dalvik.*

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class * implements android.os.Parcelable {
#noinspection ShrinkerUnresolvedReference
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#不要混淆DbBean所有子类的属性与方法
#-keepclasseswithmembers class * extends com.picclife.smart.core.db.greenDao.bean.DbBean{
#    <fields>;
#    <methods>;
#}

#表示不混淆R文件中的所有静态字段
-keep class **.R$* {
    public static <fields>;
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------

-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

-keepclassmembers class * extends android.webkit.WebViewClient {
#noinspection ShrinkerUnresolvedReference
    public void *(android.webkit.WebView, jav.lang.String);
}






#---------------------------------2.第三方库---------------------------------
#okhttp3
#-dontwarn com.squareup.okhttp3.**
#-keep class com.squareup.okhttp3.** { *;}
#-keep class okhttp3.** { *;}
#-keep class okio.** { *;}
#-dontwarn sun.security.**
#-keep class sun.security.** { *;}
#-dontwarn okio.**
#-dontwarn okhttp3.**

#retrofit2
#-dontwarn retrofit2.**
#-keep class retrofit2.** { *; }
#-keepattributes Exceptions
#-dontwarn org.robovm.**
#-keep class org.robovm.** { *; }

# RxJava RxAndroid

-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#noinspection ShrinkerUnresolvedReference
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
#noinspection ShrinkerUnresolvedReference
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

# Retrofit, OkHttp, Gson
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#-dontwarn com.squareup.okhttp.**
#-dontwarn rx.**
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#-keep class sun.misc.Unsafe { *; }
#-dontwarn java.nio.file.*
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Rxjava-promises
-keep class com.darylteo.rx.** { *; }
-dontwarn com.darylteo.rx.**

# RxJava 0.21
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# RxLifeCycle2
-keep class com.trello.rxlifecycle2.** { *; }
-keep interface com.trello.rxlifecycle2.** { *; }
-dontwarn com.trello.rxlifecycle2.**

-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.data.realm.**

# #  ############### volley混淆  ###############
# # -------------------------------------------
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
#
## For using GSON @Expose annotation
#-keepattributes *Annotation*
#
## Gson specific classes
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }


# Application classes that will be serialized/deserialized over Gson
-keep class com.picclife.smart.data.** { *; }
-keep class com.picclife.smart.data.user.** { *; }
-keep class com.picclife.smart.activities.policymodify.bean.**{ *; }
-keep class com.picclife.smart.activities.myappointment.bean.**{ *; }
-keep class com.picclife.smart.activities.InsuranceInfo.Bean.**{ *; }
-keep class com.picclife.smart.business.myclaim.bean.** {*; }
-keep class com.picclife.smart.business.mycontract.bean.** {*; }
-keep class com.picclife.smart.onlineclaim.data.** {*; }
##--------第三方沉浸式状态栏--------------
# -keep class com.gyf.immersionbar.* {*;}
# -dontwarn com.gyf.immersionbar.**

##---------------End: proguard configuration for Gson  ----------

-keep class com.darsh.multipleimageselect.** { *; }
-dontwarn com.darsh.multipleimageselect.**

-keep class com.soundcloud.android.crop.** { *; }
-dontwarn com.soundcloud.android.crop.**


#zbar扫描快，zxing可以生成和识别本地，So,我就把他们结合在了一起，这样Android二维码(条形码)功能就更便捷了 混淆配置
-keep class cn.bertsir.zbar.Qr.** { *; }
-keep class cn.bertsir.zbar.kt.** { *; }
#AndroidPdfViewer  ProGuard
-keep class com.shockwave.**

##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#版本greendao3.2.2
#-keep class org.greenrobot.greendao.**{*;}
#-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
#public static java.lang.String TABLENAME;
#public static void dropTable(org.greenrobot.greendao.database.Database, boolean);
#public static void createTable(org.greenrobot.greendao.database.Database, boolean);
#}
-keep class **$Properties
-keepclassmembers class **$Properties {*;}
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
#-dontwarn org.greenrobot.greendao.**

#微信
-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}

# ProGuard configurations for NetworkBench Lens

-keep class com.networkbench.** { *; }

-dontwarn com.networkbench.**

-keepattributes Exceptions, Signature, InnerClasses

# End NetworkBench Lens



# 支付宝钱包
-dontwarn com.alipay.**
-dontwarn HttpUtils.HttpFetcher
-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*

-dontwarn cn.org.bjca.**
-keep class cn.org.bjca.wsecx.** { *; }
-keep class cn.org.bjca.anysign.** { *; }
-keep class cn.org.bjca.xinshoushu.utils.** { *; }
-keep class cn.org.bjca.anysign.android.api.** { *; }


-dontwarn com.intsig.**
-keep class com.intsig.** { *; }

# eventbus 混淆配置
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keep enum org.greenrobot.eventbus.** { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# 泓华视频问诊

-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**


-keep class top.zibin.luban.**{*;}
-dontwarn top.zibin.luban.**

-keep class com.yanzhenjie.album.**{*;}
-dontwarn com.yanzhenjie.album.*

  #屏幕适配
-keep class me.jessyan.autosize.** { *; }
-keep interface me.jessyan.autosize.** { *; }

 #友盟混淆
-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.picclife.smart.R$*{
public static final int *;
}
# 爱加密清场SDK 混淆配置
-dontwarn com.ijm.drisk.unexp.**
-keep class com.ijm.drisk.unexp.** { *;}
-keepclasseswithmembers class * {
    native <methods>;
}


#旷世 sdk
-keep class cn.picclife.facelib.** {*;}
#旷世 网络支持retrofit2 gson
-keep class retrofit2.converter.gson.** {*;}
#旷世 网络支持 log
-keep class okhttp3.logging.** {*;}
#旷世 网络支持retrofit2
-keep class retrofit2.** {*;}

#旷世 网络支持 数据绑定 annotation core
-keep class com.fasterxml.jackson.** {*;}

#旷世 其他库
-keep class com.megvii.** {*;}
-keep class com.auth0.pljwt.** {*;}
# okhttp3
#-------------- okhttp3 start-------------
# OkHttp3
# https://github.com/square/okhttp
# okhttp
#-keepattributes *Annotation*
-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# okhttp 3
#-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
#----------okhttp end--------------



#kotlin
-keep class kotlin.** {*;}
-keep class org.intellij.lang.annotations.** {*;}
-keep class org.jetbrains.annotations.** {*;}
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
# Retrofit
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform
-keepattributes Exceptions

# okhttp
-dontwarn okio.**


#梆梆加固 开始
#-keep class com.secneo.**{*;}
#-keep class com.bangcle.**{*;}
#梆梆加固 结束

# 高德地图3D地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}
# 高德地图 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
# 高德地图 搜索
-keep   class com.amap.api.services.**{*;}

-keep class org.json.**{*;}

#arouter 开始
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

-keep class com.picclife.smart.core.comm.arouter.** {*;}
-keep class com.picclife.smart.core.comm.base.IServiceProvider.** {*;}
#如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
#arouter 结束

#融云推送 开始
# Location
-keep class com.amap.api.**{*;}
-keep class com.amap.api.services.**{*;}
-keep class com.autonavi.**{*;}
-keep class net.sqlcipher.**{*;}
-keepclassmembers class ** {
 public void onEvent*(**);
}

#5.x版本混淆规则
-keepattributes Exceptions,InnerClasses

-keepattributes Signature

-keep class io.rong.** {*;}
-keep class cn.rongcloud.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

-keep class com.picclife.smart.push.CustomPushMessageReceiver{*;}
#融云推送 结束

#小米推送 开始
-dontwarn com.xiaomi.mipush.sdk.**
-keep public class com.xiaomi.mipush.sdk.* {*; }
#小米推送 结束
#oppo 推送开始
-keep public class * extends android.app.Service
-keep class com.heytap.msp.** { *;}
#oppo 推送结束

#vivo 推送开始
-dontwarn com.vivo.push.**

-keep class com.vivo.push.**{*; }

-keep class com.vivo.vms.**{*; }

-keep class com.picclife.smart.receiver.VivoPushMessageReceiverImpl{*;}
-keep class com.picclife.smart.push.VivoPushReceiver{*;}
#vivo 推送结束

#华为推送 开始
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.hms.hatool.**{*;}
-keep class com.huawei.gamebox.plugin.gameservice.**{*;}

-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}
#华为推送 结束

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
#rx
-keep class rx.internal.util.unsafe.** { *; }
#databinding
-keep class android.databinding.** { *; }
#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

 #PictureSelector 2.0
 -keep class com.luck.picture.lib.** { *; }
-keep class com.luck.lib.camerax.** { *; }

 #Ucrop
 -dontwarn com.yalantis.ucrop**
 -keep class com.yalantis.ucrop** { *; }
 -keep interface com.yalantis.ucrop** { *; }

-keep class com.woaiqw.postprocessing** { *; }
-keep class com.woaiqw.generate** { *; }


-keep interface com.woaiqw.postprocessing** { *; }
#不要混淆IProcessing 实现类
-keep class * extends com.picclife.smart.core.comm.utils.process.IProcessing {*;}
#不要混淆IApp 实现类
#-keep class * extends com.woaiqw.postprocessing.IApp {*;}
#-keepnames class com.woaiqw.postprocessing.IApp$* {
# public <fields>;
#    public <methods>;
#}
#-keep class com.woaiqw.postprocessingannotation.App
##保留@Keep注解的类，保留
#-keep @com.woaiqw.postprocessingannotation.App class * {*;}
 #Okio
 -dontwarn org.codehaus.mojo.animal_sniffer.*

 -keep class com.chad.library.adapter.base.viewholder** { *; }
 -keep interface com.chad.library.adapter.base.viewholder** { *; }
 -keep class cn.picclife.exocr** { *; }
 -keep class cn.picclife.exocr.modelr** { *; }
 -keep class com.auth0.jwt** { *; }
 -keep class com.fasterxml.jackson** { *; }
 -keep class com.secneo.**{*;}
 #梆梆威胁感知sdk 开始
 -keep class com.bangcle.**{*;}
 -dontwarn com.bangcle.**
 -keep class com.coralline.**{*;}
 -dontwarn com.coralline.**
 #梆梆威胁感知sdk 结束

#已知的实体类路径（避免忘记添加混淆） 开始
-keep class com.yundou.loans.entity.** { *; }
-keep class com.picclife.smart.core.base.entity.** { *; }
-keep class com.picclife.smart.core.base.constat.** { *; }
-keep class com.picclife.smart.core.comm.entity.** { *; }
-keep class com.picclife.smart.core.comm.event.** { *; }
-keep class com.picclife.smart.core.db.entity.** { *; }
-keep class com.picclife.smart.core.db.enums.** { *; }
-keep class com.picclife.smart.core.db.room.bean.** { *; }
-keep class com.picclife.smart.core.http.entity.** { *; }
-keep class com.picclife.smart.claim.data.** { *; }
-keep class com.picclife.smart.debugkit.entity.** { *; }
-keep class com.picclife.smart.insurance.data.** { *; }
-keep class com.picclife.smart.login.bean.** { *; }
-keep class com.picclife.smart.main.entity.** { *; }
-keep class com.picclife.smart.message.entity.** { *; }
-keep class com.picclife.smart.policymodify.entity.** { *; }
-keep class com.picclife.smart.add.bean.** { *; }
#已知的实体类路径（避免忘记添加混淆） 结束

#梆梆安全键盘sdk 开始
-keep class com.bangcle.safekb**{*;}
#梆梆安全键盘sdk 结束

#cn.hx.plugin.ui 为前面配置的 packageBase
-keep class com.yundou.loans.ui.** {*;}

# 保持citypicker库的类不被混淆
-keep class com.example.citypicker.** { *; }
-keep class com.github.crazyandcoder.citypicker.** { *; }

#ocr 标准化项目混淆规则
-keep class android.**{*;}
#faster.jackon
-keep class com.fasterxml.jackson** {*;}
-keep class com.google.gson.**{*;}
-keep class dalvik.annotation.**{*;}
-keep class java.**{*;}
-keep class javax.**{*;}
-keep class okhttp3.**{*;}
-keep class org.jetbrains.**{*;}
-keep class org.json.**{*;}
-keep class retrofit2.**{*;}

-keep class exocr.bankcard.**{*;}
-keep class exocr.cardrec.**{*;}
-keep class exocr.exocrengine.**{*;}
-keep class exocr.idcard.**{*;}
-keep class exocr.engine.**{*;}
-keep class cn.picclife.exocr.**{*;}

-keep class com.secneo.**{*;}
-keep class com.fort.andJni.**{*;}

-keep class com.auth0.jwt.**{*;}
-keep class * implements com.picclife.smart.core.comm.widget.SwipeLayout$Designer
-keep class org.java_websocket.**{*;}

# 不混淆open sdk, 避免有些调用（如js）找不到类或方法
-keep class com.tencent.connect.** {*;}
-keep class com.tencent.open.** {*;}
-keep class com.tencent.tauth.** {*;}


-keep public class com.tencent.lbssearch.** {*;}
-keep public class com.tencent.tencentmap.**{*;}
-keep public class com.tencent.tmsbeacon.**{*;}
-keep public class com.tencent.tmsbeacon.**{*;}
-dontwarn com.qq.**
-dontwarn com.tencent.**

-keepattributes *Annotation*
-keepclassmembers class ** {
    public void on*Event(...);
}
-keep public class com.tencent.location.**{
    public protected *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}


-keep class XI.CA.XI.**{*;}
-keep class XI.K0.XI.**{*;}
-keep class XI.XI.K0.**{*;}
-keep class XI.xo.XI.XI.**{*;}
-keep class com.asus.msa.SupplementaryDID.**{*;}
-keep class com.asus.msa.sdid.**{*;}
-keep class com.bun.lib.**{*;}
-keep class com.bun.miitmdid.**{*;}
-keep class com.huawei.hms.ads.identifier.**{*;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class com.zui.opendeviceidlibrary.**{*;}
-keep class org.json.**{*;}
-keep public class com.netease.nis.sdkwrapper.Utils {public
<methods>;}

-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**
-dontwarn  android.location.Location
-dontwarn  android.net.wifi.WifiManager
-dontnote ct.**


-keep class com.tencent.** {*;}
-keep class com.tencent.mmkv.** {*;}

-obfuscationdictionary bt-proguard.txt
-classobfuscationdictionary bt-proguard.txt
-packageobfuscationdictionary bt-proguard.txt

