package com.yundou.loans.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.bigkoo.pickerview.configure.PickerOptions
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.contrarywind.view.WheelView
import java.util.*


/**
 *@ClassName LongTimePickerBuilder
 *@Deseription TODO
 *@author：wangmingyu
 *@date：2021/3/2020:45
 */
class LongTimePickerBuilder() {
    private var mPickerOptions: PickerOptions = PickerOptions(PickerOptions.TYPE_PICKER_TIME)
    var isLongTime=true
    constructor(context: Context, listener: OnTimeSelectListener) : this() {
        mPickerOptions.context = context
        mPickerOptions.timeSelectListener = listener
    }
    
    //Option
    fun setGravity(gravity: Int): LongTimePickerBuilder {
        mPickerOptions.textGravity = gravity
        return this
    }
    fun isLongTime(show: Boolean): LongTimePickerBuilder {
        isLongTime = show
        return this
    }
    fun addOnCancelClickListener(
        cancelListener: View.OnClickListener): LongTimePickerBuilder {
        mPickerOptions.cancelListener = cancelListener
        return this
    }
    
    /**
     * new boolean[]{true, true, true, false, false, false}
     * control the "year","month","day","hours","minutes","seconds " display or hide.
     * 分别控制“年”“月”“日”“时”“分”“秒”的显示或隐藏。
     *
     * @param type 布尔型数组，长度需要设置为6。
     * @return LongTimePickerBuilder
     */
    fun setType(type: BooleanArray): LongTimePickerBuilder {
        mPickerOptions.type = type
        return this
    }
    
    fun setSubmitText(textContentConfirm: String): LongTimePickerBuilder {
        mPickerOptions.textContentConfirm = textContentConfirm
        return this
    }
    
    fun isDialog(isDialog: Boolean): LongTimePickerBuilder {
        mPickerOptions.isDialog = isDialog
        return this
    }
    
    fun setCancelText(textContentCancel: String): LongTimePickerBuilder {
        mPickerOptions.textContentCancel = textContentCancel
        return this
    }
    
    fun setTitleText(textContentTitle: String?): LongTimePickerBuilder {
        mPickerOptions.textContentTitle = textContentTitle
        return this
    }
    
    fun setSubmitColor(textColorConfirm: Int): LongTimePickerBuilder {
        mPickerOptions.textColorConfirm = textColorConfirm
        return this
    }
    
    fun setCancelColor(textColorCancel: Int): LongTimePickerBuilder {
        mPickerOptions.textColorCancel = textColorCancel
        return this
    }
    
    /**
     * ViewGroup 类型的容器
     *
     * @param decorView 选择器会被添加到此容器中
     * @return LongTimePickerBuilder
     */
    fun setDecorView(decorView: ViewGroup): LongTimePickerBuilder {
        mPickerOptions.decorView = decorView
        return this
    }
    
    fun setBgColor(bgColorWheel: Int): LongTimePickerBuilder {
        mPickerOptions.bgColorWheel = bgColorWheel
        return this
    }
    
    fun setTitleBgColor(bgColorTitle: Int): LongTimePickerBuilder {
        mPickerOptions.bgColorTitle = bgColorTitle
        return this
    }
    
    fun setTitleColor(textColorTitle: Int): LongTimePickerBuilder {
        mPickerOptions.textColorTitle = textColorTitle
        return this
    }
    
    fun setSubCalSize(textSizeSubmitCancel: Int): LongTimePickerBuilder {
        mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel
        return this
    }
    
    fun setTitleSize(textSizeTitle: Int): LongTimePickerBuilder {
        mPickerOptions.textSizeTitle = textSizeTitle
        return this
    }
    
    fun setContentTextSize(textSizeContent: Int): LongTimePickerBuilder {
        mPickerOptions.textSizeContent = textSizeContent
        return this
    }
    
    /**
     * 设置最大可见数目
     *
     * @param count suggest value: 3, 5, 7, 9
     */
    fun setItemVisibleCount(count: Int): LongTimePickerBuilder {
        mPickerOptions.itemsVisibleCount = count
        return this
    }
    
    /**
     * 透明度是否渐变
     *
     * @param isAlphaGradient true of false
     */
    fun isAlphaGradient(isAlphaGradient: Boolean): LongTimePickerBuilder {
        mPickerOptions.isAlphaGradient = isAlphaGradient
        return this
    }
    
    /**
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     *
     * @param date
     * @return LongTimePickerBuilder
     */
    fun setDate(date: Calendar): LongTimePickerBuilder {
        mPickerOptions.date = date
        return this
    }
    
    fun setLayoutRes(res: Int, customListener: CustomListener): LongTimePickerBuilder {
        mPickerOptions.layoutRes = res
        mPickerOptions.customListener = customListener
        return this
    }
    
    
    /**
     * 设置起始时间
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     */
    fun setRangDate(startDate: Calendar, endDate: Calendar): LongTimePickerBuilder {
        mPickerOptions.startDate = startDate
        mPickerOptions.endDate = endDate
        return this
    }
    /**
     * 设置起始时间
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     */
    fun setStartDate(startDate: Calendar?): LongTimePickerBuilder {
        mPickerOptions.startDate = startDate
        return this
    }
      /**
     * 设置起始时间
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     */
    fun setEndDate(endDate: Calendar?): LongTimePickerBuilder {
          mPickerOptions.endDate = endDate
        return this
    }
    
    
    /**
     * 设置间距倍数,但是只能在1.0-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float): LongTimePickerBuilder {
        mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier
        return this
    }
    
    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    fun setDividerColor(@ColorInt dividerColor: Int): LongTimePickerBuilder {
        mPickerOptions.dividerColor = dividerColor
        return this
    }
    
    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    fun setDividerType(dividerType: WheelView.DividerType): LongTimePickerBuilder {
        mPickerOptions.dividerType = dividerType
        return this
    }
    
    /**
     * [.setOutSideColor] instead.
     *
     * @param backgroundId color resId.
     */
    @Deprecated("")
    fun setBackgroundId(backgroundId: Int): LongTimePickerBuilder {
        mPickerOptions.outSideColor = backgroundId
        return this
    }
    
    /**
     * 显示时的外部背景色颜色,默认是灰色
     *
     * @param outSideColor
     */
    fun setOutSideColor(@ColorInt outSideColor: Int): LongTimePickerBuilder {
        mPickerOptions.outSideColor = outSideColor
        return this
    }
    
    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    fun setTextColorCenter(@ColorInt textColorCenter: Int): LongTimePickerBuilder {
        mPickerOptions.textColorCenter = textColorCenter
        return this
    }
    
    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    fun setTextColorOut(@ColorInt textColorOut: Int): LongTimePickerBuilder {
        mPickerOptions.textColorOut = textColorOut
        return this
    }
    
    fun isCyclic(cyclic: Boolean): LongTimePickerBuilder {
        mPickerOptions.cyclic = cyclic
        return this
    }
    
    fun setOutSideCancelable(cancelable: Boolean): LongTimePickerBuilder {
        mPickerOptions.cancelable = cancelable
        return this
    }
    
    fun setLunarCalendar(lunarCalendar: Boolean): LongTimePickerBuilder {
        mPickerOptions.isLunarCalendar = lunarCalendar
        return this
    }
    
    
    fun setLabel(
        label_year: String, label_month: String, label_day: String, label_hours: String,
        label_mins: String, label_seconds: String): LongTimePickerBuilder {
        mPickerOptions.label_year = label_year
        mPickerOptions.label_month = label_month
        mPickerOptions.label_day = label_day
        mPickerOptions.label_hours = label_hours
        mPickerOptions.label_minutes = label_mins
        mPickerOptions.label_seconds = label_seconds
        return this
    }
    
    /**
     * 设置X轴倾斜角度[ -90 , 90°]
     *
     * @param x_offset_year    年
     * @param x_offset_month   月
     * @param x_offset_day     日
     * @param x_offset_hours   时
     * @param x_offset_minutes 分
     * @param x_offset_seconds 秒
     * @return
     */
    fun setTextXOffset(
        x_offset_year: Int, x_offset_month: Int, x_offset_day: Int, x_offset_hours: Int,
        x_offset_minutes: Int, x_offset_seconds: Int): LongTimePickerBuilder {
        mPickerOptions.x_offset_year = x_offset_year
        mPickerOptions.x_offset_month = x_offset_month
        mPickerOptions.x_offset_day = x_offset_day
        mPickerOptions.x_offset_hours = x_offset_hours
        mPickerOptions.x_offset_minutes = x_offset_minutes
        mPickerOptions.x_offset_seconds = x_offset_seconds
        return this
    }
    
    fun isCenterLabel(isCenterLabel: Boolean): LongTimePickerBuilder {
        mPickerOptions.isCenterLabel = isCenterLabel
        return this
    }
    
    /**
     * @param listener 切换item项滚动停止时，实时回调监听。
     * @return
     */
    fun setTimeSelectChangeListener(
        listener: OnTimeSelectChangeListener): LongTimePickerBuilder {
        mPickerOptions.timeSelectChangeListener = listener
        return this
    }

    /**
     * 是否展示长期有效字段
     * @param isShow Boolean
     * @return LongTimePickerBuilder
     */
    fun  isShowLongTime(isShow:Boolean): LongTimePickerBuilder{
        isLongTime=isShow
        return this
    }
    fun build(): LongTimePickerView {
        return LongTimePickerView(mPickerOptions,isLongTime)
    }
}