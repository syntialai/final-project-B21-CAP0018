package com.qhope.core.api.source.response.transaction

data class TransactionDetailResponse(

  var created_at: Long? = null,

  var hospital: TransactionHospitalResponse? = null,

  var id: String? = null,

  var payment: TransactionPaymentResponse? = null,

  var room_type: String? = null,

  var status: String? = null,

  var total: Double? = null,

  var updated_at: Long? = null,

  var user: TransactionUserResponse? = null,

  var referral_letter: UploadReferralLetterResponse? = null,

  var selected_date: Long? = null
)