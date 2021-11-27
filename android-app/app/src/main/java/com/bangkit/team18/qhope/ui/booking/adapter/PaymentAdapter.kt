package com.bangkit.team18.qhope.ui.booking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bangkit.team18.core.domain.model.booking.PaymentType
import com.bangkit.team18.qhope.databinding.LayoutPaymentMethodsBinding
import com.bangkit.team18.qhope.ui.base.adapter.BaseAdapter
import com.bangkit.team18.qhope.ui.base.adapter.BaseDiffCallback
import com.bangkit.team18.qhope.ui.booking.callback.PaymentCallback

class PaymentAdapter(
  private val paymentCallback: PaymentCallback
) : BaseAdapter<PaymentType, LayoutPaymentMethodsBinding>(diffCallback) {

  companion object {
    private val diffCallback = object : BaseDiffCallback<PaymentType>() {
      override fun contentEquality(oldItem: PaymentType, newItem: PaymentType): Boolean {
        return oldItem == newItem
      }
    }
  }

  override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutPaymentMethodsBinding
    get() = LayoutPaymentMethodsBinding::inflate

  override fun getViewHolder(binding: LayoutPaymentMethodsBinding): BaseViewHolder {
    return PaymentViewHolder(binding)
  }

  inner class PaymentViewHolder(binding: LayoutPaymentMethodsBinding): BaseViewHolder(binding) {

    private val paymentMethodAdapter: PaymentMethodsAdapter by lazy {
      PaymentMethodsAdapter(paymentCallback)
    }

    override fun bind(data: PaymentType) {
      with(binding) {
        tvPaymentType.text = data.name
        rvPaymentMethodsByType.adapter = paymentMethodAdapter
        paymentMethodAdapter.submitList(data.methods)
      }
    }
  }
}