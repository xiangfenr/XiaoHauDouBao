package com.yundou.loans.adapter


import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.yundou.loans.coreui.R
import com.yundou.loans.coreui.databinding.AsrAgreementAdapterItemBinding
import  com.yundou.loans.entity.WrbAgreeMentBean
import  com.yundou.loans.entity.ZxdNewAgreeBean
import  com.yundou.loans.ui.CommonWebViewActivity
import  com.yundou.loans.widget.clickNoRepeat

/**
 * 微融宝,协议Adapter
 */
class WrbAgreementAdapter :
    BaseQuickAdapter<WrbAgreeMentBean, BaseDataBindingHolder<AsrAgreementAdapterItemBinding>>(R.layout.asr_agreement_adapter_item) {
    @SuppressLint("SetTextI18n")


    private val checkedState = mutableMapOf<Int, Boolean>()

    override fun convert(
        holder: BaseDataBindingHolder<AsrAgreementAdapterItemBinding>,
        item: WrbAgreeMentBean,
    ) {



        holder.dataBinding?.let {
            it.xieyiTv.text = item.agreementName

            val position = holder.bindingAdapterPosition  // 获取当前 item 位置
            if (position == RecyclerView.NO_POSITION) return // 防止 -1 位置异常

            it.sragreementCheckbox.isChecked = checkedState[position]!!
            it.sragreementCheckbox.setOnCheckedChangeListener { compoundButton, b ->
                checkedState[position] = b
            }

            it.root.clickNoRepeat {
                val intent = Intent(context, CommonWebViewActivity::class.java)
                intent.putExtra("webUrl", item.agreementUrl)
                context.startActivity(intent)
            }

        }

    }

    /**
     * 在 setList() 时初始化 checkedState，防止为空
     */
    override fun setList(list: Collection<WrbAgreeMentBean>?) {
        super.setList(list)
        checkedState.clear()  // 清空旧数据，防止复用问题
        list?.forEachIndexed { index, _ ->
            checkedState.put(index, false)  // 默认所有 CheckBox 未选中
        }
    }


    /**
     * 判断是否所有checkebox 都选中
     *
     */
    fun areaAllChecked(): Boolean {
        return data.isNotEmpty() && checkedState.values.all { it }
    }

}