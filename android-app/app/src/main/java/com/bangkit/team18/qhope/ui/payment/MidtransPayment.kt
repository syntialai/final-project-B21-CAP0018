package com.bangkit.team18.qhope.ui.payment

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.bangkit.team18.core.BuildConfig
import com.midtrans.sdk.corekit.callback.CheckoutCallback
import com.midtrans.sdk.corekit.callback.TransactionCallback
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.callback.TransactionOptionsCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.PaymentType
import com.midtrans.sdk.corekit.core.SdkCoreFlowBuilder
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.PaymentMethodsModel
import com.midtrans.sdk.corekit.models.TransactionResponse
import com.midtrans.sdk.corekit.models.snap.EnabledPayment
import com.midtrans.sdk.corekit.models.snap.Token
import com.midtrans.sdk.corekit.models.snap.Transaction
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.PaymentMethods
import timber.log.Timber


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
//    SdkUIFlowBuilder.init()
//      .setClientKey(BuildConfig.MIDTRANS_CLIENT_KEY)
//      .setContext(context)
//      .setTransactionFinishedCallback(getTransactionFinishedCallback())
//      .setMerchantBaseUrl(com.midtrans.sdk.corekit.BuildConfig.BASE_URL)
////      .setSelectedPaymentMethods(getPaymentMethodModels(context))
//      .enableLog(true)
//      .setColorTheme(CustomColorTheme("#2b90bf", "#1d5f8a", "#eb9570"))
//      .setLanguage("en")
//      .buildSDK()

    SdkCoreFlowBuilder.init(
      context,
      BuildConfig.MIDTRANS_CLIENT_KEY,
      BuildConfig.QHOPE_API_URL
    )
      .enableLog(true)
      .buildSDK()
  }

  fun initTransactionRequest(): TransactionRequest {
    // Create new Transaction Request
    val transactionRequestNew =
      TransactionRequest(System.currentTimeMillis().toString() + "iniItuId", 666000.0)
    transactionRequestNew.customerDetails = initCustomerDetails()
//    transactionRequestNew.gopay = Gopay("mysamplesdk:://midtrans")
//    transactionRequestNew.shopeepay = Shopeepay("mysamplesdk:://midtrans")
    return transactionRequestNew
  }

  fun getCheckoutCallback(successCallback: () -> Unit): CheckoutCallback =
    object : CheckoutCallback {
      override fun onSuccess(token: Token) {
        // Checkout token will be used to charge the transaction later
        val checkoutToken: String = token.tokenId
        // Action when succeded
        getTransactionOptions(checkoutToken, successCallback)
      }

      override fun onFailure(token: Token?, reason: String?) {
        // Action when failed
        Timber.d("getCheckoutCallback failure ${token?.tokenId} $reason")
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show()
      }

      override fun onError(error: Throwable) {
        // Action when error
        Timber.d("getCheckoutCallback error ${error.message}")
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
      }
    }

  fun getTransactionOptions(checkoutToken: String, successCallback: () -> Unit) =
    MidtransSDK.getInstance()
      .getTransactionOptions(checkoutToken, object : TransactionOptionsCallback {
        override fun onSuccess(transaction: Transaction) {
          // List of enabled payment method string
          val enabledPayments: List<EnabledPayment> = transaction.enabledPayments
          for (i in enabledPayments)
            Timber.d("Ceker ${i.type} ${i.category} ${i.status} ${i.acquirer}")
          successCallback.invoke()
        }

        override fun onFailure(transaction: Transaction?, reason: String?) {
          Timber.d("getTransactionOptions failure $reason")
          Toast.makeText(context, reason, Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: Throwable) {
          Timber.d("getTransactionOptions error ${error.message}")
          Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        }
      })

  private fun initCustomerDetails(): CustomerDetails {
    //define customer detail (mandatory for coreflow)
    return CustomerDetails("First", "Last", "maulanadiaz@gmail.com", "081234567899")
  }

  fun getTransactionCallback(): TransactionCallback = object : TransactionCallback {
    override fun onError(p0: Throwable?) {
      Timber.d("getTransactionCallback error ${p0?.message}")
      Toast.makeText(context, p0?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(p0: TransactionResponse?) {
      Timber.d("Ceker ${p0?.statusCode} ${p0?.statusMessage} ${p0?.fraudStatus} ${p0?.transactionStatus}")
      Timber.d("Ceker ${p0?.bniVaNumber}")
    }

    override fun onFailure(p0: TransactionResponse?, p1: String?) {
      Timber.d("getTransactionCallback failure $p1")
      Toast.makeText(context, p1, Toast.LENGTH_SHORT).show()
    }

  }

  fun getTransactionFinishedCallback(): TransactionFinishedCallback {
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