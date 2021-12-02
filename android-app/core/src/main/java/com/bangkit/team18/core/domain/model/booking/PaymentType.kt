package com.bangkit.team18.core.domain.model.booking

data class PaymentType(

  val id: String,

  val name: String,

  val methods: List<PaymentMethod>
)
