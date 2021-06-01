package com.bangkit.team18.core.domain.usecase.interactor

import android.net.Uri
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.repository.UserRepository
import com.bangkit.team18.core.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow

class UserInteractor(private val userRepository: UserRepository) : UserUseCase {
  override suspend fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>> =
    userRepository.uploadUserImage(userId, imageUri)

  override suspend fun addUser(userId: String, user: User): Flow<ResponseWrapper<Boolean>> =
    userRepository.addUser(userId, user)

  override suspend fun getUser(userId: String): Flow<ResponseWrapper<User?>> =
    userRepository.getUser(userId)
}