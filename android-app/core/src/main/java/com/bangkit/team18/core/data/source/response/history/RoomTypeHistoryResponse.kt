package com.bangkit.team18.core.data.source.response.history

data class RoomTypeHistoryResponse(

  val id: String,

  val name: String
) {
  constructor() : this(
    "",
    ""
  )
}
