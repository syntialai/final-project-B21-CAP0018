package com.bangkit.team18.core.domain.model.history

data class HistoryDetail(

    val id: String,

    val hospitalName: String,

    val hospitalImagePath: String,

    val startDate: String,

    val endDate: String,

    val nightCount: Int?,

    val status: HistoryStatus
)