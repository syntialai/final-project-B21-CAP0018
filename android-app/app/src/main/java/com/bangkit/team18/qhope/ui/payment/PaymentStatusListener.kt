package com.bangkit.team18.qhope.ui.payment

interface PaymentStatusListener {

  fun onPaymentSuccess(transactionId: String)

  fun onPaymentPending(transactionId: String)

  fun onPaymentFailed(statusMessage: String)

  fun onPaymentCancelled()
}