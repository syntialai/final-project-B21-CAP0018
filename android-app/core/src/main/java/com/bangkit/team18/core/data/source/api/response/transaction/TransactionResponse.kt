package com.bangkit.team18.core.data.source.api.response.transaction

data class TransactionResponse(

  var id: String? = null,

  var hospital_name: String? = null,

  var hospital_image: String? = null,

  var created_at: String? = null,

  var night_count: Int? = null,

  var status: HistoryStatus? = null
)