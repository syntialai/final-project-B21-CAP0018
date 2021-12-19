package com.qhope.app.ui.tnc.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.qhope.app.databinding.LayoutTermsAndConditionBinding
import com.qhope.app.ui.base.adapter.BaseAdapter
import com.qhope.app.ui.base.adapter.BaseDiffCallback

class TermsAndConditionAdapter :
  BaseAdapter<Pair<String, String>, LayoutTermsAndConditionBinding>(diffCallback) {

  companion object {
    private val diffCallback = object : BaseDiffCallback<Pair<String, String>>() {
      override fun contentEquality(
        oldItem: Pair<String, String>,
        newItem: Pair<String, String>
      ): Boolean {
        return oldItem == newItem
      }
    }
  }

  override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutTermsAndConditionBinding
      get() = LayoutTermsAndConditionBinding::inflate

  override fun getViewHolder(binding: LayoutTermsAndConditionBinding): BaseViewHolder {
    return ViewHolder(binding)
  }

  inner class ViewHolder(binding: LayoutTermsAndConditionBinding) : BaseViewHolder(binding) {

    @SuppressLint("SetTextI18n")
    override fun bind(data: Pair<String, String>) {
      val indexChar = ('A'.code + bindingAdapterPosition).toChar()
      binding.apply {
        tvTitle.text =  "$indexChar. ${data.first}"
        tvDescription.text = data.second
      }
    }
  }
}