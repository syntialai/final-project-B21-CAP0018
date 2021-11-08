package com.bangkit.team18.core.data.repository

import android.content.Context
import android.content.SharedPreferences

class AuthSharedPrefRepository private constructor(context: Context) {

  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

  companion object {
    private const val SHARED_PREFERENCES_NAME = "Authentication"
    private const val ID_TOKEN = "idToken"
    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"

    fun newInstance(context: Context) = AuthSharedPrefRepository(context)
  }

  fun clearSharedPrefs() {
    sharedPreferences.edit().clear().apply()
  }

  var idToken: String
    get() = sharedPreferences.getString(ID_TOKEN, "").orEmpty()
    set(idToken) {
      sharedPreferences.edit().putString(ID_TOKEN, idToken).apply()
    }

  var userId: String?
    get() = sharedPreferences.getString(USER_ID, null)
    set(userId) {
      sharedPreferences.edit().putString(USER_ID, userId).apply()
    }

  var userName: String
    get() = sharedPreferences.getString(USER_NAME, "").orEmpty()
    set(name) {
      sharedPreferences.edit().putString(USER_NAME, name).apply()
    }

  fun updateUser(id: String, name: String) {
    this.userId = id
    this.userName = name
  }
}