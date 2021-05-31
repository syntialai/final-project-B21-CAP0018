package com.bangkit.team18.core.domain.usecase

import android.net.Uri
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserUseCase {

  fun addUser(userId: String, user: User): Flow<ResponseWrapper<Boolean>>

  fun getUserData(userId: String): Flow<ResponseWrapper<User>>

  fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>>
}