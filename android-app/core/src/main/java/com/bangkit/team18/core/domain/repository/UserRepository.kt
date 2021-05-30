package com.bangkit.team18.core.domain.repository

import android.net.Uri
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
  fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>>
  fun addUser(userId: String, user: User): Flow<ResponseWrapper<Boolean>>
}