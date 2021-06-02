package com.bangkit.team18.core.data.source

import android.net.Uri
import com.bangkit.team18.core.data.source.response.user.UserResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
  suspend fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>>
  suspend fun updateUser(userId: String, data: HashMap<String, Any?>)
  suspend fun getUser(userId: String): Flow<UserResponse?>
  suspend fun uploadUserKtp(userId: String, uri: Uri): Flow<ResponseWrapper<Uri>>
  suspend fun uploadUserSelfie(userId: String, uri: Uri): Flow<ResponseWrapper<Uri>>
}