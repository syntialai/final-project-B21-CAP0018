package com.bangkit.team18.qhope.model.history

data class History(

    val id: String,

    val hospitalName: String,

    val hospitalImage: String,

    val startDate: String,

    val endDate: String,

    val status: HistoryStatus
)