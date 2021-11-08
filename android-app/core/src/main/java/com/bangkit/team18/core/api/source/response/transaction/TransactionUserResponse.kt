package com.bangkit.team18.core.api.source.response.transaction

data class TransactionUserResponse(

  var birth_date: Int? = null,

  var email: String? = null,

  var id: String? = null,

  var name: String? = null,

  var nik: String? = null,

  var gender: String? = null,

  val phone_number: String? = null
)