package com.bangkit.team18.qhope.ui.booking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.team18.core.domain.model.booking.PaymentMethod
import com.bangkit.team18.qhope.databinding.LayoutPaymentMethodByTypeBinding
import com.bangkit.team18.qhope.ui.base.adapter.BaseAdapter
import com.bangkit.team18.qhope.ui.base.adapter.BaseDiffCallback
import com.bangkit.team18.qhope.ui.booking.callback.PaymentCallback
import com.bumptech.glide.Glide

class PaymentMethodsAdapter(
  private val paymentCallback: PaymentCallback
) : BaseAdapter<PaymentMethod, LayoutPaymentMethodByTypeBinding>(diffCallback) {

  companion object {
    private val diffCallback = object : BaseDiffCallback<PaymentMethod>() {
      override fun contentEquality(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem == newItem
      }
    }
  }

  override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutPaymentMethodByTypeBinding
    get() = LayoutPaymentMethodByTypeBinding::inflate

  override fun getViewHolder(binding: LayoutPaymentMethodByTypeBinding): BaseViewHolder {
    return PaymentViewHolder(binding)
  }

  inner class PaymentViewHolder(binding: LayoutPaymentMethodByTypeBinding) :
    BaseViewHolder(binding) {

    override fun bind(data: PaymentMethod) {
      with(binding) {
        rbPaymentMethod.isChecked = false

        tvPaymentMethodName.text = data.name
        Glide.with(mContext)
          .load(data.image)
          .into(ivPaymentMethodLogo)
        rbPaymentMethod.isChecked = data.checked
        rbPaymentMethod.setOnCheckedChangeListener { _, isChecked ->
          if (bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return@setOnCheckedChangeListener
          }

          val paymentMethod = getItem(bindingAdapterPosition)

          paymentMethod.checked = isChecked
          paymentCallback.onPaymentSelected(paymentMethod.parentId, paymentMethod)
        }
      }
    }
  }
}