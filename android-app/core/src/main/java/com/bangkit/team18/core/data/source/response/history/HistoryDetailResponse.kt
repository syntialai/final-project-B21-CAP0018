package com.bangkit.team18.core.data.source.response.history

import com.google.firebase.Timestamp

data class HistoryDetailResponse(

  val id: String,

  val hospitalName: String,

  val hospitalImagePath: String,

  val startDate: Timestamp,

  val endDate: Timestamp,

  val status: String
)