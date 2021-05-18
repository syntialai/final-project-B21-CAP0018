package com.bangkit.team18.qhope.model.booking

import com.google.android.gms.maps.model.LatLng

data class HospitalDetail(

    var id: String? = null,

    var name: String? = null,

    var image: String? = null,

    var type: String? = null,

    var description: String? = null,

    var address: String? = null,

    var location: LatLng? = null,

    var telephone: String? = null,

    var availableRoomCount: Int? = null
)