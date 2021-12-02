package com.bangkit.team18.core.domain.model.booking

data class PaymentMethod(

  val id: String,

  val parentId: String,

  val name: String,

  val image: String,

  var checked: Boolean = false,
)