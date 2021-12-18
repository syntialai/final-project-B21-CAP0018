package com.qhope.core.data.mapper

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object DataMapper {

  fun toFormattedPrice(price: Double): String = NumberFormat.getCurrencyInstance().apply {
    maximumFractionDigits = 0
    currency = Currency.getInstance(Locale("in", "ID"))
  }.format(price)
}