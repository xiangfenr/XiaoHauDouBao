package com.yundou.loans.ui.mine

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.yundou.loans.R
import com.yundou.loans.adapter.BottomAdapter
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.ActivityMainBinding
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.fragment.HomeFragment
import com.yundou.loans.ui.fragment.MineFragment


@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : CommonActivity<UserViewModel, ActivityMainBinding>() {

    private val homeFragmentPage: HomeFragment = HomeFragment()
    private val mineFragmentPage: MineFragment = MineFragment()

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun init() {
        val list = mutableListOf<Fragment>()
        list.add(homeFragmentPage)
        list.add(mineFragmentPage)
        mBinding.vp.adapter = BottomAdapter(supportFragmentManager, list)
        mBinding.vp.setOnTouchListener { _, _ ->
            true // 返回true表示触摸事件被消费掉，不会传递到子视图
        }


        mBinding.bv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    mBinding.vp.currentItem = 0
                }
                R.id.navigation_mine -> {
                    mBinding.vp.currentItem = 1
                }
            }
            true
        }
    }
}