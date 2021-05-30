package com.bangkit.team18.core.domain.model.history

data class History(

  val id: String,

  val hospitalName: String,

  val hospitalImagePath: String,

  val createdAt: String,

  val nightCount: Int?,

  val status: HistoryStatus
)