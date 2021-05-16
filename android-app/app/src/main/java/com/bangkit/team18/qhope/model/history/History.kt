package com.bangkit.team18.qhope.model.history

import java.util.Date

data class History(

    var id: String? = null,

    var hospitalName: String? = null,

    var hospitalImage: String? = null,

    var startDate: Date? = null,

    var endDate: Date? = null,

    var status: HistoryStatus? = null
)