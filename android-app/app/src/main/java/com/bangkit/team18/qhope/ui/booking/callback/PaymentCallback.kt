package com.bangkit.team18.qhope.ui.booking.callback

import com.bangkit.team18.core.domain.model.booking.PaymentMethod

interface PaymentCallback {

  fun onPaymentSelected(paymentTypeId: String, paymentMethod: PaymentMethod)
}