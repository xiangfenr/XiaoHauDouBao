package com.yundou.loans.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseFragment
import com.yundou.loans.databinding.FragmentMineLayoutBinding
import com.yundou.loans.entity.DaikuanUrlData
import com.yundou.loans.model.LoginViewModel
import com.yundou.loans.ui.loan.*
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.ui.mine.LoginActivity
import com.yundou.loans.ui.mine.PrivacyActivity
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.Utils
import com.yundou.loans.widget.CommonDialog
import com.yundou.loans.widget.clickNoRepeat


class MineFragment : BaseFragment<LoginViewModel, FragmentMineLayoutBinding>() {

    private var logbackDialog: CommonDialog? = null
    private var logoutDialog: CommonDialog? = null
    private var daikuaiUrlData: DaikuanUrlData? = null


    override fun layoutId(): Int {
        return R.layout.fragment_mine_layout
    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initListener()
        mBinding.versiontv.text = "当前版本: ${BaseApp.context.version}"
        inData()
    }


    private fun inData() {
        val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
        val phone = MmkvUtil.getInstance().decodeString("loginphone")
        val isEditForm = MmkvUtil.getInstance().decodeBoolean(phone + Constants.IS_EDIT_FORM)
        mBinding.tvMyName.text = Utils.hidePhoneNumber(phone)

        val phone_number_status = MmkvUtil.getInstance().decodeInt(Constants.PHONE_NUMBER_STATUS)
        //2、应用商店审核人员账号(客户端切到本服)  4=提交给应用商店的测试账号(客户端切到本服)
        if (phone_number_status==2 || phone_number_status==4) {
            mBinding.daikuanNumTv.text = "23345"
            mBinding.daikuanNumTv.textSize = 34f
            mBinding.tvv.text = "立即还款"

            mBinding.tvv.clickNoRepeat { item ->
                //测试账号直接跳转到详情 普通账号判断是否提交过表单
                activity?.startActivity(Intent(activity, DetailActivity::class.java))
            }
        } else {
            mBinding.daikuanNumTv.text = "当前暂无还款"
            mBinding.daikuanNumTv.textSize = 14f
            mBinding.tvv.text = "暂无还款"
        }



    }


    private fun initListener() {
        val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")


        mBinding.tvPassword.clickNoRepeat {
            val intent = Intent(activity, PassWordActivity::class.java)
            activity?.startActivity(intent)
        }

        mBinding.tvZhuce.clickNoRepeat {
            val intent = Intent(activity, CommonWebViewActivity::class.java)
            intent.putExtra("webUrl", BaseApp.context.zhuhceXieyi)
            activity?.startActivity(intent)
        }
        mBinding.yinsi.clickNoRepeat {
            val intent = Intent(activity, PrivacyActivity::class.java)
            intent.putExtra("webUrl", BaseApp.context.yinsi)
            activity?.startActivity(intent)
        }
        //问题反馈
        mBinding.tvReturn.clickNoRepeat {
            activity?.startActivity(Intent(activity, FeedbackActivity::class.java))
        }


        //注销
        mBinding.logback.clickNoRepeat {

            if (logbackDialog == null)
                logbackDialog = CommonDialog.Builder(getActivity())
                    .setContentView(R.layout.dialog_cancel)
                    .setCancelable(true)
                    .setGravity(Gravity.CENTER)
                    .setCanceledOnTouchOutside(true)
                    .setPercentWidth(0.8f)
                    .create()

            logbackDialog?.findViewById<TextView>(R.id.content)?.text = "是否要注销账号？"
            logbackDialog?.findViewById<TextView>(R.id.tv_diss)
                ?.clickNoRepeat { logbackDialog?.dismiss() }
            logbackDialog?.findViewById<TextView>(R.id.tv_ok)?.clickNoRepeat {

                viewModel.logoBack {
                    logout()
                }
                logbackDialog?.dismiss()
            }
            logbackDialog?.show()
        }
        //退出
        mBinding.logout.clickNoRepeat {

            if (logoutDialog == null)
                logoutDialog = CommonDialog.Builder(getActivity())
                    .setContentView(R.layout.dialog_cancel)
                    .setCancelable(true)
                    .setGravity(Gravity.CENTER)
                    .setCanceledOnTouchOutside(true)
                    .setPercentWidth(0.8f)
                    .create()
            logoutDialog?.findViewById<TextView>(R.id.content)?.text = "是否要退出登录？"
            logoutDialog?.findViewById<TextView>(R.id.tv_diss)
                ?.clickNoRepeat { logoutDialog?.dismiss() }
            logoutDialog?.findViewById<TextView>(R.id.tv_ok)?.clickNoRepeat {
                logout()
                logoutDialog?.dismiss()
            }
            logoutDialog?.show()
        }

    }

    private fun intentFormActivity(cls: Class<*>) {
        if (BaseApp.context.storeid == Constants.CHANNEL_OPPO) {
            XPopup.Builder(context).asConfirm(
                "", "您当前正在签约${getString(R.string.app_name)}借贷产品"
            ) {
                activity?.startActivity(Intent(activity, cls))
            }.show()
        } else {
            activity?.startActivity(Intent(activity, cls))
        }
    }


    //退出登录 清除缓存token信息
    private fun logout() {
        MmkvUtil.getInstance().removeKey("token")
        MmkvUtil.getInstance().removeKey("partner_id")
        MmkvUtil.getInstance().removeKey(Constants.IS_EDIT_FORM)

        MyApplication.isForm = false

        activity?.startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }


    override fun onDestroy() {
        super.onDestroy()

        if (logoutDialog != null) {
            logoutDialog?.dismiss()
            logoutDialog = null
        }

        if (logbackDialog != null) {
            logbackDialog?.dismiss()
            logbackDialog = null
        }

    }
}