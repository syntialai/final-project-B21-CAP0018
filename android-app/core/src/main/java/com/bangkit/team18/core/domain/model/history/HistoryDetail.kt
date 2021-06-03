package com.bangkit.team18.core.domain.model.history

data class HistoryDetail(

    val id: String,

    val hospitalId: String,

    val hospitalName: String,

    val hospitalImagePath: String,

    val hospitalAddress: String,

    val hospitalType: String,

    val roomCostPerDay: String,

    val roomType: RoomTypeHistory,

    val referralLetterFileName: String,

    val referralLetterFileUrl: String,

    val bookedAt: String,

    val startDate: String,

    val endDate: String,

    val nightCount: Int?,

    val status: HistoryStatus,

    val user: UserHistory
)