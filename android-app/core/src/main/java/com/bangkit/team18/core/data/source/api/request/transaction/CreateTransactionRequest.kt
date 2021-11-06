package com.bangkit.team18.core.data.source.api.request.transaction

data class CreateTransactionRequest(

  var hospital_id: String? = null,

  var room_type_id: String? = null,

  var selectedDateTime: Long? = null,

  var referral_letter_url: String? = null,

  var referral_letter_name: String? = null,

  var payment_type: String? = null
)
