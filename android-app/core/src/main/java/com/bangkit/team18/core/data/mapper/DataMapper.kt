package com.bangkit.team18.core.data.mapper

import java.text.NumberFormat
import java.util.*

object DataMapper {

  fun <Response, Model> mapToModels(
    responses: List<Response>,
    mapper: (Response) -> Model
  ): List<Model> {
    return responses.map {
      mapper.invoke(it)
    }
  }

  fun toFormattedPrice(price: Double) = NumberFormat.getCurrencyInstance().apply {
    maximumFractionDigits = 0
    currency = Currency.getInstance(Locale("in", "ID"))
  }.format(price)
}