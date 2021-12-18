package com.qhope.app.ui.payment

interface PaymentStatusListener {

  fun onPaymentSuccess(transactionId: String)

  fun onPaymentPending(transactionId: String)

  fun onPaymentFailed(statusMessage: String)

  fun onPaymentCancelled()
}