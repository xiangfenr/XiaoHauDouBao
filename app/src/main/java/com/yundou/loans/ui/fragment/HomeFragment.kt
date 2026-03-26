package com.yundou.loans.ui.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.adapter.LoansAdapter
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseFragment
import com.yundou.loans.databinding.FragmentHomeLayoutBinding
import com.yundou.loans.entity.ChannerItem
import com.yundou.loans.entity.DaikuanUrlData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.QuanLiuChengFormLoanActivity
import com.yundou.loans.ui.loan.*
import com.yundou.loans.ui.mine.BkGuideActivity
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.ui.mine.HkGuideActivity
import com.yundou.loans.ui.mine.JkGuideActivity
import com.yundou.loans.utils.*
import com.yundou.loans.widget.clickNoRepeat
import com.yundou.loans.widget.gone
import com.yundou.loans.widget.visible


@RequiresApi(Build.VERSION_CODES.N)
class HomeFragment : BaseFragment<UserViewModel, FragmentHomeLayoutBinding>() {

    private var loansAdapter: LoansAdapter? = null
    private var partner_id: Int = -1
    private lateinit var channerManager: ChannelManager

    private var loan_overdue_status: Int = 0    // 逾期处理 1关闭  2打开
    private var sink = 0 //贷超刷新逻辑控制
    private var firstLoading = false


    override fun layoutId(): Int {
        return R.layout.fragment_home_layout
    }

    @SuppressLint("HardwareIds")
    override fun onResume() {
        super.onResume()
        val isForm = MmkvUtil.getInstance().decodeBoolean(Constants.IS_EDIT_FORM)
        if (isForm) {
            initModel()
        }
    }

    private fun initModel() {
        //获取域名
        viewModel.geVersion {
            it?.let {
                loan_overdue_status = it.loan_overdue_status
                MmkvUtil.getInstance().encode(Constants.ZXD_CLICKDEAL, it.zxd_click_deal)
                MyApplication.shrimp_channel_concurrency = it.shrimp_channel_concurrency
                MyApplication.timeout_second = it.timeout_second
                MyApplication.jiyong_price = it.jiyong_price ?: 0.0
                sink = it.sink

                if (partner_id != Constants.PARTNER_BENBU || BaseApp.context.storeid == Constants.CHANNEL_QQ) {//是否提交过表单
//                    是否提交过表单
                    isFormCheck(it)
                }

                // 逾期处理 1关闭  2打开
//                if (loan_overdue_status == 2) {
//                    mBinding.homeYuqi.setImageResource(R.mipmap.zhaiquan)
//                } else {
//                    mBinding.homeYuqi.setImageResource(R.mipmap.hk)
//                }
            }


        }
    }


    private fun getDatalist() {
        val age = MmkvUtil.getInstance().decodeInt(Constants.IDCARD_AGE)

        viewModel.getDataList(age) {
            if (it.list.isNullOrEmpty())
                return@getDataList


            var currentList: List<ChannerItem> = ArrayList()
            if (!firstLoading) {
                it.list?.let { list ->
                    loansAdapter?.setList(it.list)
                    currentList = list
                    firstLoading = true
                }
            } else {
                channerManager.setChannelList(it.list as ArrayList<ChannerItem>)
                channerManager.refreshData(sink)
                loansAdapter?.setList(channerManager.getChannelList())
                currentList = channerManager.getChannelList()
                Log.e("sunjiayuan", "getDatalist: " + channerManager.getChannelList())
            }

            if (currentList.isNotEmpty()) {
                //表单有数据展示
                mBinding.loanTitle.visible()
                mBinding.listview.loanListRecyc.visible()
                viewModel.reportPointRequest(6)

                val item = currentList[1]
                item.let {
                    mBinding.daikuanNumTv.text = item.loan_limit
                    mBinding.lilvTv.setText("日利率${item.daily}")
                }
            } else {
                //表单有数据展示
                mBinding.loanTitle.visible()
                mBinding.listview.loanListRecyc.visible()

            }
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
        Log.i("xiang", "HomeFragment页面: partner_id: " + partner_id)
        initModel()
        initAdapter()
        channerManager = ChannelManager()

        mBinding.txtFeedbackSubmit.clickNoRepeat {
            applyClick()
        }
        mBinding.product.clickNoRepeat {
            applyClick()
        }
        mBinding.xf.clickNoRepeat {
            val intent = Intent(requireActivity(), CommonWebViewActivity::class.java)
            intent.putExtra("webUrl", "file:///android_asset/tongyongdaikuan.html")
            startActivity(intent)
        }
        mBinding.ed.clickNoRepeat {
            applyClick()
        }
        mBinding.te.clickNoRepeat {
            applyClick()
        }

        mBinding.jk.clickNoRepeat {
            activity?.startActivity(Intent(activity, JkGuideActivity::class.java))
        }
        mBinding.huankuanImg.clickNoRepeat {
            activity?.startActivity(Intent(activity, HkGuideActivity::class.java))
        }

        mBinding.dk.clickNoRepeat {
            activity?.startActivity(Intent(activity, BkGuideActivity::class.java))
        }


        // 下拉刷新处理
        mBinding.refreshLayout.setOnRefreshListener { //调接口添加数据
            initModel()
            //关闭头部布局
            mBinding.refreshLayout.finishRefresh()
        }

        mBinding.homeYuqi.setOnClickListener {
            // 逾期处理 1关闭  2打开
            if (loan_overdue_status == 2) {
                val phone = MmkvUtil.getInstance().decodeString("loginphone")
                val hash = hashMapOf(
                    "mobile" to phone,
                    "name" to ""
                )
                val content =
                    DkyqCryptoUtils.encryptPhone(Gson().toJson(hash), Constants.YQDKCL_PUBLICK_KEY)

                val url = "${Constants.YQDKCL_WEBURL}$content"
                val intent = Intent(requireActivity(), CommonWebViewActivity::class.java)
                intent.putExtra("webUrl", url)
                startActivity(intent)
            } else {
                activity?.startActivity(Intent(activity, HkGuideActivity::class.java))
            }
        }

    }

    private fun applyClick() {
        if (mBinding.listview.loanListRecyc.isVisible) {
            loansAdapter?.data?.size?.let { size ->
                if (size > 0) {
                    val index = if (size == 1) {
                        0
                    } else {
                        1
                    }
                    val data = loansAdapter?.data?.get(index) as ChannerItem
                    channerManager.clickItem(data.id ?: 0)

                    data.loan_id?.let {
                        viewModel.viewLoanNew(it) {
                            val intent = Intent(activity, CommonWebViewActivity::class.java)
                            intent.putExtra("webUrl", it)
                            activity?.startActivity(intent)
                        }
                    }
                } else {
                    viewModel.defUI.toastEvent.postValue("暂无产品")
                }
            }
        } else {
            when (partner_id) {
                Constants.PARTNER_BENBU -> {
                    intentFormActivity(ChoiceOneActivity::class.java)
                }

                Constants.PARTNER_SHENGR -> {
                    if (!MyApplication.shrimp_channel_concurrency.isNullOrEmpty()) {
                        intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                    } else {
                        intentFormActivity(SRChoiceOneActivity::class.java)
                    }
                }

                Constants.PARTNER_TXFQ -> {
                    if (!MyApplication.shrimp_channel_concurrency.isNullOrEmpty()) {
                        intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                    } else {
                        intentFormActivity(TXFQFormOneActivity::class.java)
                    }
                }

                Constants.PARTNER_MOLI -> {
                    intentFormActivity(MoLiOneFormActivity::class.java)
//                     if (!MyApplication.shrimp_channel_concurrency.isNullOrEmpty()) {
//                        intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
//                    } else {
//                        intentFormActivity(MoLiOneFormActivity::class.java)
//                    }
                }

                Constants.PARTNER_TWOP -> {
                    intentFormActivity(TwoProOneFormActivity::class.java)
                }

                Constants.PARTNER_JIYONGQB -> {
                    if (!MyApplication.shrimp_channel_concurrency.isNullOrEmpty()) {
                        intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                    } else {
                        intentFormActivity(JIYQBFormOneActivity::class.java)
                    }
                }

                Constants.PARTNER_JIYONGBANG -> {
                    intentFormActivity(JIYBangFormOneActivity::class.java)
                }

                Constants.PARTNER_YOUQIANQB -> {
                    intentFormActivity(YouQianOneActivity::class.java)
                }

                Constants.PARTNER_ZXD -> {
                    intentFormActivity(ZxdFormActivity::class.java)
                }

                Constants.PARTNER_KYD -> {
                    intentFormActivity(KydFormActivity::class.java)
                }

                Constants.PARTNER_YUANXIAOHUA -> {
                    if (!MyApplication.shrimp_channel_concurrency.isNullOrEmpty()) {
                        intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                    } else {
                        intentFormActivity(YuanXiaoHuaFormOneActivity::class.java)
                    }
                }

                Constants.PARTNER_QIDAI -> {

                    val phone = MmkvUtil.getInstance().decodeString("loginphone") ?: ""
                    viewModel.qiDaiQueryFormStatus(phone) {
                        //表单是否完成 0=完成 1=未完成
                        if (it == 0) {
                            //完成表单. 查询产品
                            intentFormActivity(QiDaiProduceActivity::class.java)
                        } else {
                            intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                        }
                    }
                }

                Constants.PARTNER_JIDAI, Constants.PARTNER_YUEXIANG -> {
                    if (!MyApplication.shrimp_channel_concurrency.isNullOrEmpty()) {
                        intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                    } else {
                        intentFormActivity(JiDaiFormActivity::class.java)
                    }

                }

                Constants.PARTNER_SHANDAIMIAO -> {
                    intentFormActivity(QuanLiuChengFormLoanActivity::class.java)
                }
            }
            viewModel.reportPointRequest(2)
        }
    }

    private fun intentFormActivity(cls: Class<*>) {

        if (BaseApp.context.storeid == Constants.CHANNEL_OPPO) {
            viewModel.oppoIntercept {
                XPopup.Builder(context).asConfirm(
                    "", "您当前正在签约${getString(R.string.app_name)}借贷产品"
                ) {
                    activity?.startActivity(Intent(activity, cls))
                }.show()
            }
        } else {
            activity?.startActivity(Intent(activity, cls))
        }
    }

    /**
     * 贷超展示逻辑
     *  表单状态：form_status    1=关闭 2=打开
     *  贷超列表状态：loan_status 1=无 2=有
     *             //true 提交过表单 展示贷超列表
     * 表单状态form_statu=1 关闭
     */
    private fun isFormCheck(bean: DaikuanUrlData) {
        // 表单状态form_status 1=关闭，2=打开
        if (bean.form_status == 1) {  //表单关闭
            judegeIsLoanStatus(bean.loan_status)
            MyApplication.isForm = false
        } else {  //表单打开
            val isForm = MmkvUtil.getInstance().decodeBoolean(Constants.IS_EDIT_FORM)
            if (isForm) {
                judegeIsLoanStatus(bean.loan_status)
            } else {
                mBinding.txtFeedbackSubmit.visible()
                //form_status=2打开  loan_status=2打开 isFrom=false 隐藏贷超,按钮点击去填写表单
                if (bean.form_status == 2 && bean.loan_status == 2) {
                    mBinding.listview.loanListRecyc.gone()
                    mBinding.loanTitle.gone()
                }
            }
        }
        if (bean.form_status == 1 && bean.loan_status == 1) {
            mBinding.txtFeedbackSubmit.gone()
            mBinding.listview.loanListRecyc.gone()
            mBinding.loanTitle.gone()
        }
    }

    //贷超列表状态 loan_status 1=关闭,2=打开
    private fun judegeIsLoanStatus(loan_status: Int) {
        mBinding.txtFeedbackSubmit.visible()
        val phone_number_status = MmkvUtil.getInstance().decodeInt(Constants.PHONE_NUMBER_STATUS)
        //2、应用商店审核人员账号(客户端切到本服)  4=提交给应用商店的测试账号(客户端切到本服)
        if (loan_status == 2 && phone_number_status in listOf(
                AccountType.NORMAL_NEW_USER.code,
                AccountType.SELF_TEST_PHONE.code
            )
        ) {
            getDatalist()
        } else {
            mBinding.listview.loanListRecyc.gone()
            mBinding.loanTitle.gone()
        }
    }


    /*
     *初始化adapter
     */
    private fun initAdapter() {
        loansAdapter = LoansAdapter()
        mBinding.listview.loanListRecyc.layoutManager = LinearLayoutManager(context)
        mBinding.listview.loanListRecyc.adapter = loansAdapter
        getOnClick()

    }

    private fun getOnClick() {
        loansAdapter?.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as ChannerItem
            channerManager.clickItem(data.id ?: 0)
            data.loan_id?.let {
                viewModel.viewLoanNew(it) {
                    val intent = Intent(activity, CommonWebViewActivity::class.java)
                    intent.putExtra("webUrl", it)
                    activity?.startActivity(intent)
                }
            }
        }
    }


}