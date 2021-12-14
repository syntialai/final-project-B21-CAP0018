package com.bangkit.team18.qhope.ui.payment

import android.content.Context
import com.bangkit.team18.core.BuildConfig
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class MidtransPayment(
  private val context: Context,
  private val paymentStatusListener: PaymentStatusListener
) {

  fun setupPayments() {
    SdkUIFlowBuilder.init()
      .setClientKey(BuildConfig.MIDTRANS_CLIENT_KEY)
      .setContext(context)
      .setTransactionFinishedCallback(getTransactionFinishedCallback())
      .setMerchantBaseUrl(BuildConfig.QHOPE_API_URL)
      .setUIkitCustomSetting(uiKitCustomSetting())
//      .setSelectedPaymentMethods(getPaymentMethodModels(context))
      .enableLog(true)
      .setColorTheme(CustomColorTheme("#2b90bf", "#1d5f8a", "#eb9570"))
      .setLanguage("en")
      .buildSDK()
  }

  fun initTransactionRequest(transactionId: String): TransactionRequest {
    return TransactionRequest(transactionId, 1.0)
  }

  private fun getTransactionFinishedCallback(): TransactionFinishedCallback {
    return TransactionFinishedCallback { result ->
      result.response?.let { response ->
        val transactionId = response.transactionId

//        val type = response.paymentType
//        val code = response.paymentCode
//        val codeResponse = response.paymentCodeResponse
//        val expiration = response.bniExpiration
//        val transactionStatus = response.transactionStatus
//        val statusCode = response.statusCode
//        val statusMessage = response.statusMessage
//        Timber.d("Ceker type $type")
//        Timber.d("Ceker code $code")
//        Timber.d("Ceker codeResponse $codeResponse")
//        Timber.d("Ceker expiration $expiration")
//        Timber.d("Ceker transactionStatus $transactionStatus")
//        Timber.d("Ceker statusCode $statusCode")
//        Timber.d("Ceker statusMessage $statusMessage")
//
//        for(i in response.accountNumbers){
//          Timber.d("Ceker VA ${i.accountNumber}")
//          Timber.d("Ceker BANK ${i.bank}")
//        }
//        response.bank

        when (result.status) {
          TransactionResult.STATUS_SUCCESS ->
            paymentStatusListener.onPaymentSuccess(transactionId)
          TransactionResult.STATUS_PENDING ->
            paymentStatusListener.onPaymentPending(transactionId)
          TransactionResult.STATUS_FAILED ->
            paymentStatusListener.onPaymentFailed(response.statusMessage)
        }
      } ?: run {
        if (result.isTransactionCanceled) {
          paymentStatusListener.onPaymentCancelled()
        } else {
          paymentStatusListener.onPaymentFailed(result.response.statusMessage)
        }
      }
    }
  }

  private fun uiKitCustomSetting(): UIKitCustomSetting {
    val uIKitCustomSetting = UIKitCustomSetting()
    uIKitCustomSetting.isSkipCustomerDetailsPages = true
    uIKitCustomSetting.isShowPaymentStatus = true
    return uIKitCustomSetting
  }

}