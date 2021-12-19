package com.qhope.core.domain.model.booking

data class PaymentType(

  val id: String,

  val name: String,

  val methods: List<PaymentMethod>
)
