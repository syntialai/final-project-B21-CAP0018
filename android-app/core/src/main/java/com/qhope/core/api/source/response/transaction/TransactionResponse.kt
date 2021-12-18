package com.qhope.core.api.source.response.transaction

data class TransactionResponse(

  var id: String? = null,

  var hospital_name: String? = null,

  var hospital_image: String? = null,

  var created_at: Long? = null,

  var night_count: Int? = null,

  var status: String? = null
)