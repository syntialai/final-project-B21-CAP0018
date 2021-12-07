package com.bangkit.team18.core.domain.model.home

data class Hospital(

  val id: String,

  val name: String,

  val image: String,

  val type: String,

  val address: String,

  val availableRoomCount: Int
)
