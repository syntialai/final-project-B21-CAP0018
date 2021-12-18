package com.qhope.core.api.source.request.transaction

data class CreateTransactionRequest(

  var hospital_id: String? = null,

  var room_type_id: String? = null,

  var selected_date_time: Long? = null,

  var referral_letter_url: String? = null,

  var referral_letter_name: String? = null,
)
