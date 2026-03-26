package com.yundou.loans.ui.loan


import android.content.Intent
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.yundou.loans.R
import com.yundou.loans.adapter.WmAdapter
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.WmXieyiLayoutBinding
import com.yundou.loans.entity.WmInfoData
import com.yundou.loans.entity.WmUserData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat


class WmXieYIActivity : CommonActivity<UserViewModel, WmXieyiLayoutBinding>() {

    private var groupAdapter: WmAdapter? = null
    private var choiceid: String? = null

    override fun getLayoutId(): Int {
        return R.layout.wm_xieyi_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "专属额度"
    }

    override fun init() {

        val userData = intent.getSerializableExtra("shengrongdata") as? WmUserData

        initAdapter()

        if (userData?.products != null && userData?.products?.size != 0) {
            groupAdapter?.setList(userData?.products)
        }

        mBinding.submit.clickNoRepeat {
            if (TextUtils.isEmpty(choiceid)) {
                viewModel.defUI.toastEvent.postValue("请勾选并同意个人信息授权书")
                return@clickNoRepeat
            }

            viewModel.wMapply(choiceid) {
                startActivity(Intent(this, WmSuccessActivity::class.java))
                finish()
            }
        }
    }

    /*
    *初始化adapter
    */
    private fun initAdapter() {
        groupAdapter = WmAdapter()
        mBinding.recycler.layoutManager = LinearLayoutManager(this)
        mBinding.recycler.adapter = groupAdapter

        groupAdapter?.addChildClickViewIds(R.id.cb_login)
        groupAdapter?.setOnItemChildClickListener { adapter, v, position ->
            val data = adapter.data[position] as WmInfoData
            when (v.id) {
                R.id.cb_login -> {
                    choiceid = data.id.toString()
                    viewModel.agreement(choiceid)
                }
            }

        }
    }

}