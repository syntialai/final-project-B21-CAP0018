package com.bangkit.team18.core.domain.model.history

data class History(

    val id: String,

    val hospitalName: String,

    val hospitalImage: String,

    val startDate: String,

    val endDate: String,

    // TODO: Get by -> val nightCount = TimeUnit.MILLISECONDS.toDays(endDate.time - data.startDate.time.orZero()).toInt()
    val nightCount: Int?,

    val status: HistoryStatus
)