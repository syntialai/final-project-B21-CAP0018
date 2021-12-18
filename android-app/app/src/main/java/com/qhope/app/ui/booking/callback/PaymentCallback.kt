package com.qhope.app.ui.booking.callback

import com.qhope.core.domain.model.booking.PaymentMethod

interface PaymentCallback {

  fun onPaymentSelected(paymentTypeId: String, paymentMethod: PaymentMethod)
}