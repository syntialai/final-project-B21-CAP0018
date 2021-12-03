package com.bangkit.team18.qhope.ui.payment

import android.content.Context
import android.text.TextUtils
import com.bangkit.team18.core.BuildConfig
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.PaymentType
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.PaymentMethodsModel
import com.midtrans.sdk.corekit.models.snap.EnabledPayment
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.PaymentMethods
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class MidtransPayment(
  private val context: Context,
  private val paymentStatusListener: PaymentStatusListener
) {

  companion object {
    const val CATEGORY_VA = "ATM/Bank Transfer"
    private const val PAYMENT_NAME_PERMATA_VA = "Permata VA"
    private const val PAYMENT_NAME_BCA_VA = "BCA VA"
    private const val PAYMENT_NAME_BNI_VA = "BNI VA"
    private const val PAYMENT_NAME_BRI_VA = "BRI VA"
    private const val PAYMENT_NAME_OTHER_VA = "Other VA"
    private const val PAYMENT_NAME_MANDIRI_ECHANNEL = "Mandiri VA"
  }

  fun setupPayments() {
    SdkUIFlowBuilder.init()
      .setClientKey(BuildConfig.MIDTRANS_CLIENT_KEY)
      .setContext(context)
      .setTransactionFinishedCallback(getTransactionFinishedCallback())
      .setMerchantBaseUrl(com.midtrans.sdk.corekit.BuildConfig.BASE_URL)
      .setSelectedPaymentMethods(getPaymentMethodModels(context))
      .enableLog(true)
      .setColorTheme(CustomColorTheme("#2b90bf", "#1d5f8a", "#eb9570"))
      .setLanguage("en")
      .buildSDK()
  }

  private fun getTransactionFinishedCallback() : TransactionFinishedCallback {
    return TransactionFinishedCallback { result ->
      result.response?.let { response ->
        val transactionId = response.transactionId
        when (result.status) {
          TransactionResult.STATUS_SUCCESS ->
            paymentStatusListener.onPaymentSuccess(transactionId)
          TransactionResult.STATUS_PENDING ->
            paymentStatusListener.onPaymentPending(transactionId)
          TransactionResult.STATUS_FAILED ->
            paymentStatusListener.onPaymentFailed(response.statusMessage)
        }
//        result.getResponse().getValidationMessages()
      } ?: run {
        if (result.isTransactionCanceled) {
          paymentStatusListener.onPaymentCancelled()
        } else {
          paymentStatusListener.onPaymentFailed(result.response.statusMessage)
        }
      }
    }
  }

  private fun getPaymentMethodModels(context: Context): ArrayList<PaymentMethodsModel> {
    val enabledPayments = getEnabledPayments()
    val paymentMethods = enabledPayments.map { enabledPayment ->
      getVaPaymentMethods(context, enabledPayment.type, enabledPayment.status)
    }
    return ArrayList(paymentMethods)
  }

  private fun getEnabledPayments(): List<EnabledPayment> {
    return arrayListOf(
      EnabledPayment(PaymentType.PERMATA_VA, CATEGORY_VA),
      EnabledPayment(PaymentType.BCA_VA, CATEGORY_VA),
      EnabledPayment(PaymentType.BNI_VA, CATEGORY_VA),
      EnabledPayment(PaymentType.BRI_VA, CATEGORY_VA),
      EnabledPayment(PaymentType.ALL_VA, CATEGORY_VA),
      EnabledPayment(PaymentType.E_CHANNEL, CATEGORY_VA)
    )
  }

  private fun getVaPaymentMethods(
    context: Context,
    type: String,
    status: String
  ): PaymentMethodsModel {
    var model = PaymentMethods.getMethods(context, type, status, false)
    if (model == null && !TextUtils.isEmpty(type)) {
      when (type) {
        PaymentType.PERMATA_VA -> model =
          PaymentMethodsModel(PAYMENT_NAME_PERMATA_VA, null, 0, "", 4, status)
        PaymentType.BCA_VA -> model =
          PaymentMethodsModel(PAYMENT_NAME_BCA_VA, null, 0, "", 2, status)
        PaymentType.BNI_VA -> model =
          PaymentMethodsModel(PAYMENT_NAME_BNI_VA, null, 0, "", 5, status)
        PaymentType.BRI_VA -> model =
          PaymentMethodsModel(PAYMENT_NAME_BRI_VA, null, 0, "", 6, status)
        PaymentType.ALL_VA -> model =
          PaymentMethodsModel(PAYMENT_NAME_OTHER_VA, null, 0, "", 7, status)
        PaymentType.E_CHANNEL -> model =
          PaymentMethodsModel(PAYMENT_NAME_MANDIRI_ECHANNEL, null, 0, "", 3, status)
      }
    }
    return model
  }
}