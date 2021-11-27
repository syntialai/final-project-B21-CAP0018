package com.bangkit.team18.qhope.ui.booking.view

import android.view.View
import com.bangkit.team18.core.data.mapper.DataMapper
import com.bangkit.team18.core.domain.model.booking.PaymentMethod
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentPaymentBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel
import com.bangkit.team18.qhope.ui.booking.adapter.PaymentAdapter
import com.bangkit.team18.qhope.ui.booking.callback.PaymentCallback
import com.bangkit.team18.qhope.utils.ClickSpan

class PaymentFragment : BaseFragment<FragmentPaymentBinding, BaseViewModel>(
  FragmentPaymentBinding::inflate,
  BaseViewModel::class
), PaymentCallback {

  private val paymentAdapter by lazy {
    PaymentAdapter(this)
  }

  override fun setupViews() {
    with(binding) {
      recyclerViewPaymentMethods.adapter

      ClickSpan.setClickable(textViewPaymentPolicyAgreement, getString(R.string.privacy_policy)) {
        // TODO
        showToast(R.string.privacy_policy)
      }
      ClickSpan.setClickable(textViewPaymentPolicyAgreement, getString(R.string.terms_of_use)) {
        // TODO
        showToast(R.string.terms_of_use)
      }
    }
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        buttonPay -> { /* TODO */ }
      }
    }
  }

  override fun setupObserver() {
    super.setupObserver()

  }

  override fun onPaymentSelected(paymentTypeId: String, paymentMethod: PaymentMethod) {
    TODO("Not yet implemented")
  }

  override fun showLoadingState(isLoading: Boolean) {
    super.showLoadingState(isLoading)
    toggleShowPayment(isLoading.not())
  }

  private fun setPaymentData() {
    with(binding) {
      textViewPaymentBookingId.text = ""
      textViewPaymentBookedAt.text = ""
      textViewPayTotal.text = ""
    }
  }

  private fun setRoomData(hospitalName: String, roomType: RoomType) {
    with(binding.layoutPaymentSelectedRoomInfo) {
      textViewBookingSelectedRoom.text = getString(
        R.string.selected_room_type,
        roomType.name,
        hospitalName
      )
      textViewBookingSelectedRoomPrice.text = DataMapper.toFormattedPrice(roomType.price)
    }
  }

  private fun toggleButtonState(enabled: Boolean) {
    binding.buttonPay.isEnabled = enabled
  }

  private fun toggleShowPayment(show: Boolean) {
    with(binding) {
      layoutPayment.showOrRemove(show)
      layoutPay.showOrRemove(show)
    }
  }
}