package com.bangkit.team18.qhope.ui.history.adapter

import com.google.firebase.storage.StorageReference

interface HistoryItemCallback {

  fun onClickListener(id: String)

  fun getStorageRef(imagePath: String): StorageReference
}