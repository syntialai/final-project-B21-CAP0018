package com.bangkit.team18.qhope.ui.home.adapter

import com.google.firebase.storage.StorageReference

interface HomeHospitalItemCallback {

  fun onClickListener(id: String)

  fun getStorageRef(imagePath: String): StorageReference
}