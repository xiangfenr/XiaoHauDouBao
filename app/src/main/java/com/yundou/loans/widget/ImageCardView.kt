package com.yundou.loans.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.luck.picture.lib.utils.DensityUtil
import com.yundou.loans.R

/**
 * 定义一个viewCard 用于装饰 上传资料展示
 *
 * @author：wangmingyu
 * @date：2020/4/16 15:58
 */
class ImageCardView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : FrameLayout(mContext, attrs, defStyleAttr, defStyleRes), View.OnClickListener {
    private var view: View? = null

    //主背景view
    private var im_bg: ImageView? = null

    //添加按钮
    private var im_add: ImageView? = null

    //删除按钮
    private var im_close: ImageView? = null

    //上传进度
    private var tv_loading_progress: TextView? = null

    //错误展示
    private var tv_error: TextView? = null

    //添加按钮下方，上传提示语
    private var tv_des: TextView? = null
    private var cl_card_back: ConstraintLayout? = null

    //按钮icon
    private var addIcon = 0

    //背景icon
    private var bgIcon = 0

    //关闭icon
    private var closeIcon = 0

    //自定义属性
    private var typeArray: TypedArray? = null

    //事件监听
    private var listener: OnImageCardViewListener? = null

    //图片宽高
    var v_width = 0
    var v_height = 0
    var path: String? = null
    private var idRes = 0
    fun setListener(listener: OnImageCardViewListener?) {
        this.listener = listener
    }

    fun setListener(addImageClick: (View?) -> Unit, closeClick: (View?) -> Unit) {

        this.listener = object : OnImageCardViewListener {
            override fun onAddClick(view: View?) {
                addImageClick.invoke(view)
            }

            override fun onCloseClick(view: View?) {
                closeClick.invoke(view)
            }

        }
    }

    /**
     * 初始化啊view
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRe
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRe: Int) {
        view = inflate(context, R.layout.layout_card_view, this)
        view?.let {


            im_bg = it.findViewById(R.id.im_bg)
            im_add = it.findViewById(R.id.im_add)
            tv_des = it.findViewById(R.id.tv_des)
            im_close = it.findViewById(R.id.im_del)
            cl_card_back = it.findViewById(R.id.cl_card_back)
            tv_loading_progress = it.findViewById(R.id.tv_loading_progress)
            tv_error = it.findViewById(R.id.tv_error)
            typeArray = context.obtainStyledAttributes(attrs, R.styleable.ImageCardView)
            if (typeArray != null) {
                //获取是否要显示左边按钮
                val isClose = typeArray!!.getBoolean(R.styleable.ImageCardView_is_close, false)
                if (isClose) {
                    im_close?.visibility = VISIBLE
                } else {
                    im_close?.visibility = INVISIBLE
                }
                val isAdd = typeArray!!.getBoolean(R.styleable.ImageCardView_is_add, true)
                if (isAdd) {
                    im_add?.visibility = VISIBLE
                    tv_des?.visibility = VISIBLE
                } else {
                    im_add?.visibility = GONE
                    tv_des?.visibility = GONE
                }
                //处理标题
                //先获取标题是否要显示图片icon
                addIcon = typeArray!!.getResourceId(R.styleable.ImageCardView_cv_addIcon,
                    R.mipmap.icon_card_add)
                bgIcon = typeArray!!.getResourceId(R.styleable.ImageCardView_cv_addIcon,
                    R.mipmap.icon_bill)
                closeIcon =
                    typeArray!!.getResourceId(R.styleable.ImageCardView_cv_addIcon, R.mipmap.delete)
                v_width = typeArray!!.getDimensionPixelSize(R.styleable.ImageCardView_v_width,
                    view?.width!! - DensityUtil.dip2px(getContext(), 22f))
                v_height = typeArray!!.getDimensionPixelSize(R.styleable.ImageCardView_v_height,
                    view?.height!! - DensityUtil.dip2px(getContext(), 22f))
                setImageBGViewSize(v_width, v_height)
                //            cl_card_back.addView(im_bg);
                im_bg?.setImageResource(bgIcon)
                im_add?.setImageResource(addIcon)
                im_close?.setImageResource(closeIcon)
                typeArray?.recycle()
                im_close?.setOnClickListener(this)
                im_add?.setOnClickListener(this)
                tv_error?.setOnClickListener(this)
            }
        }
    }

    fun setShowClose(isShow: Boolean) {
        if (isShow) {
            im_close!!.visibility = VISIBLE
        } else {
            if (im_close!!.visibility == VISIBLE) im_close!!.visibility = INVISIBLE
        }
    }

    fun setImageBGViewSize(v_width: Int, v_height: Int) {
        if (im_bg != null) {
            val layoutParams = im_bg!!.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = v_height
            layoutParams.width = v_width
            im_bg!!.layoutParams = layoutParams
        }
    }

    fun setShowAdd(isShow: Boolean) {
        if (isShow) {
            im_add!!.visibility = VISIBLE
            tv_des!!.visibility = VISIBLE
        } else if (im_add!!.visibility == VISIBLE) {
            im_add!!.visibility = GONE
            tv_des!!.visibility = GONE
        }
    }

    val isAddShow: Boolean
        get() = im_add?.visibility == VISIBLE

    fun setImageAddIcon(@IdRes idres: Int) {
        if (idres != -1) {
            addIcon = idres
            im_add!!.setImageResource(addIcon)
        }
    }

    fun setDes(des: String?) {
        if (tv_des != null) tv_des!!.text = des
    }

    fun setDesHide() {
        if (tv_des != null) tv_des!!.visibility = GONE
    }

    fun setImageColseIcon(@IdRes idres: Int) {
        if (idres != -1) {
            closeIcon = idres
            im_close!!.setImageResource(closeIcon)
        }
    }

    /**
     * 设置图片背景
     *
     * @param idres
     */
    @SuppressLint("ResourceType")
    fun setImageBgIcon(@IdRes idres: Int) {
        if (idres != -1) {
            bgIcon = idres
            im_bg!!.setImageResource(idres)
        }
    }

    @SuppressLint("ResourceType")
//    fun setStyle() {
//        setImageBgIcon(R.mipmap.icon_bank_bill_back)
//        setImageAddIcon(R.mipmap.icon_bank_card_add)
//        setImageColseIcon(R.mipmap.delete)
//        setDes("")
//    }

    /**
     * 设置进度
     *
     * @param progress      当前进度
     * @param contentlength 文件总长度
     */
    fun setProgress(progress: Long, contentlength: Long) {
        val mge = Message()
        mge.what = (progress / contentlength * 100).toInt()
        handler.sendMessageDelayed(mge, 1000)
    }

//    val handler: Handler = object : Handler(Looper.getMainLooper(), Handler.Callback {
//        val what = it.what
//        setShowClose(false)
//        setShowAdd(false)
//        if (tv_loading_progress!!.visibility == GONE) tv_loading_progress!!.visibility = VISIBLE
//        tv_loading_progress!!.text = "$what%"
//        if (what == 100) {
//            setShowClose(true)
//            tv_loading_progress!!.visibility = GONE
//        }
//        false
//    })

    // 创建一个Handler
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val what = msg.what
            setShowClose(false)
            setShowAdd(false)
            if (tv_loading_progress!!.visibility == GONE) tv_loading_progress!!.visibility = VISIBLE
            tv_loading_progress!!.text = "$what%"
            if (what == 100) {
                setShowClose(true)
                tv_loading_progress!!.visibility = GONE
            }
        }
    }

    /**
     * 是否展示错误信息
     *
     * @param er
     */
    fun error(er: Boolean) {
        if (er) {
            tv_error!!.visibility = VISIBLE
            tv_loading_progress!!.text = ""
            tv_loading_progress!!.visibility = VISIBLE
        } else {
            tv_error!!.visibility = GONE
            tv_loading_progress!!.visibility = GONE
        }
    }

    /**
     * 根据传入地址设置图片
     *
     * @param path  图片地址
     * @param idRes 默认展示占位
     */
    fun setImagePath(path: String?, idRes: Int) {
        if (TextUtils.isEmpty(path)) return
        setShowClose(true)
        setShowAdd(false)
        error(false)
        this.path = path
        this.idRes = idRes
//        GlideUtils.getInstance().showImageHeader(this.context, path!!, im_bg, idRes)
    }

    /**
     * 根据图片地址加载图片
     *
     * @param path
     */
    fun setImagePath(path: String?) {
        if (TextUtils.isEmpty(path)) {
            reImageBg()
            return
        }
        setShowClose(true)
        setShowAdd(false)
        error(false)
        this.path = path
//        GlideUtils.getInstance()
//            .showImageHeader(context, path!!, im_bg, R.mipmap.icon_bank_bill_back)
    }

    /**
     * 根据图片地址加载图片
     *
     * @param path
     */
    fun setImagePathBase64(path: String?) {
        if (TextUtils.isEmpty(path)) {
            reImageBg()
            return
        }
        setShowClose(true)
        setShowAdd(false)
        error(false)
        this.path = path
//        GlideUtils.getInstance().loadImage(context, path, im_bg)
    }

    /**
     * 根据图片地址加载图片,
     * base64
     *
     * @param path
     */
    fun setImagePathNotClose(path: String) {
        if (TextUtils.isEmpty(path)) {
            reImageBg()
            return
        }
        setShowClose(true)
        setShowAdd(false)
        error(false)
        this.path = path
        val split = path.split(",").toTypedArray()
        if (split.size > 1) {
            setImagePathBase64(split[1])
            //            GlideUtils.getInstance().showImageView(McpApplication.getContext(), split[1], im_bg, R.mipmap.icon_bill);
        }
        //        GlideUtils.getInstance().showImageView(McpApplication.getContext(), path, im_bg, R.mipmap.icon_bill);
    }

    /**
     * 重置当前默认图片
     */
    fun reImageBg() {
        im_bg?.let { Glide.with(im_bg!!).clear(it) }
        im_bg!!.setImageResource(bgIcon)
        setShowClose(false)
        setShowAdd(true)
        error(false)
    }

    /**
     * 重置当前默认图片
     *
     * @param reIcon
     * @param mShowClose
     * @param mShowAdd
     * @param mError
     */
    fun reImageBg(reIcon: Int, mShowClose: Boolean, mShowAdd: Boolean, mError: Boolean) {
        im_bg!!.setImageResource(reIcon)
        setShowClose(mShowClose)
        setShowAdd(mShowAdd)
        error(mError)
    }

    override fun onClick(v: View) {
        if (listener != null) {
            when (v.id) {
                R.id.im_add -> {
                    listener!!.onAddClick(v)
                }
                R.id.tv_error -> {
                    listener!!.onAddClick(v)
                    error(false)
                }
                R.id.im_del -> {
                    listener!!.onCloseClick(v)
                }
            }
        }
    }

    interface OnImageCardViewListener {
        fun onAddClick(view: View?)
        fun onCloseClick(view: View?)
    }

    init {
        init(mContext, attrs, defStyleAttr, defStyleRes)
    }
}