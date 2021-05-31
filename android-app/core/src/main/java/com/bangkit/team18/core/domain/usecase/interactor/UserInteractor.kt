package com.bangkit.team18.core.domain.usecase.interactor

import android.net.Uri
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.repository.UserRepository
import com.bangkit.team18.core.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow

class UserInteractor(private val userRepository: UserRepository) : UserUseCase {

  override fun addUser(userId: String, user: User) = userRepository.addUser(userId, user)

  override fun getUserData(userId: String): Flow<ResponseWrapper<User>> {
    TODO("Not yet implemented")
  }

  override fun uploadUserImage(userId: String, imageUri: Uri) =
    userRepository.uploadUserImage(userId, imageUri)
}