package com.yundou.loans.widget

import android.content.Context
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.yundou.loans.coreui.R
import com.yundou.loans.adapter.YXHAgreementAdapter
import com.yundou.loans.adapter.YXHPlatformsAgreementAdapter
import com.yundou.loans.coreui.databinding.YuanxiaohuaAgreementDialogBinding
import com.yundou.loans.entity.MatchData


/**
 * @Author: fenr
 * 时间: 2025/4/2
 * 类名: ACTIVITY
 * 简述:
 *
 */
class YuanXiaoHuaAgreementDialog(private var context: Context) : BottomPopupView(context) {

    private lateinit var mBinding: YuanxiaohuaAgreementDialogBinding
    private var agreementData: MatchData? = null

    private val adapter by lazy { YXHAgreementAdapter() }
    private val adapterPlatforms by lazy { YXHPlatformsAgreementAdapter() }

    private var currentType = 0  // 0 =adapter  1=adapterPlatforms

    override fun getImplLayoutId(): Int {
        return R.layout.yuanxiaohua_agreement_dialog
    }

    override fun onCreate() {
        super.onCreate()

        mBinding = DataBindingUtil.bind(popupImplView)!!

        mBinding.xieyRecyclerview.layoutManager = LinearLayoutManager(context)


        //合作方逻辑
        //如果 planMatchOrgans不为空，优先展示 planMatchOrgans中的第一个产品
        //如果 planMatchOrgans为空，则展示planMatchPlatforms中patformType=5或者patformType=1的，第一个产品
        //planMatchOrgans && planMatchPlatforms 都为空的情况下，那就是没有匹配到产品
        agreementData?.let { data ->
            val organs = data.planMatchOrgans
            val organsProtocols = organs?.planMatchProtocol
            val platforms = data.planMatchPlatforms
            val platformResultList = platforms?.resultList

            if (!organsProtocols.isNullOrEmpty()) {
                val organsNonNull = organs ?: return@let
                currentType = 0

                Glide.with(this)
                    .load(organsNonNull.planMatchGoodsLogo)
                    .placeholder(R.mipmap.app_logo) // 加载中的占位图
                    .error(R.mipmap.app_logo) // 加载错误时的图片
                    .into(mBinding.imgICon)

                mBinding.jigouname.text = organsNonNull.planMatchGoodsName
//            mBinding.jigougongsiname.text = "当前已参与人数: ${it.applyNum}"


                mBinding.xieyRecyclerview.adapter = adapter

                adapter.setList(organsProtocols)
//            }else if (null!=it.planMatchPlatforms && it.planMatchPlatforms.resultList?.isNotEmpty() == true){
            } else if (platforms != null && (platforms.platformType == 5 || platforms.platformType == 1) && !platformResultList.isNullOrEmpty() && platformResultList[0] != null) {
                currentType = 1
                val firstPlatform = platformResultList[0]!!

                Glide.with(this)
                    .load(firstPlatform.platformLogo)
                    .placeholder(R.mipmap.app_logo) // 加载中的占位图
                    .error(R.mipmap.app_logo) // 加载错误时的图片
                    .into(mBinding.imgICon)

                mBinding.jigouname.text = firstPlatform.platformName
                mBinding.jigougongsiname.text = firstPlatform.organsName

                mBinding.xieyRecyclerview.adapter = adapterPlatforms
                val protocols = firstPlatform.platformProtocolList
                if (protocols.isNullOrEmpty()) {
                    mBinding.xieyRecyclerview.gone()
                    mBinding.gerenxinxixieyiwenben.gone()
                    mBinding.agreementbtn.text = "激活额度"
                } else {
                    mBinding.xieyRecyclerview.visible()
                    mBinding.agreementbtn.text = "同意协议, 激活额度"
                    mBinding.gerenxinxixieyiwenben.visible()
                    adapterPlatforms.setList(protocols)
                }
            }
        }


        mBinding.agreementbtn.setOnClickListener {
            if (currentType == 0) {
                if (adapter.areaAllChecked()) {
                    xieyiClick?.agreementClick()
                } else {
                    Toast.makeText(context, "请阅读并同意相关协议", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                if (mBinding.xieyRecyclerview.isVisible){
                    if ( adapterPlatforms.areaAllChecked()) {
                        xieyiClick?.agreementClick()
                    } else {
                        Toast.makeText(context, "请阅读并同意相关协议", Toast.LENGTH_LONG)
                            .show()
                    }
                }else{
                    xieyiClick?.agreementClick()
                }

            }


        }



        mBinding.closeImg.clickNoRepeat {
            XPopup.Builder(context).asConfirm(
                "确定要关闭吗? ", "您只需要点击下方的同意协议按钮, 就能完成您的贷款申请!"
            ) {
                dismiss()
            }.show()
        }

    }

    fun setAgreementtData(data: MatchData) {
        this.agreementData = data
    }

    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }

}