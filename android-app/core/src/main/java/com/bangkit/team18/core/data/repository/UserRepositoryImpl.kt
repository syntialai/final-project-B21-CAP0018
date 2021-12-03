package com.bangkit.team18.core.data.repository

import com.bangkit.team18.core.api.source.request.user.IdentityConfirmationRequest
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
import com.bangkit.team18.core.data.mapper.UserMapper.mapToUser
import com.bangkit.team18.core.data.repository.base.FetchDataWrapper
import com.bangkit.team18.core.data.repository.base.UpdateDataWrapper
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

@ExperimentalCoroutinesApi
class UserRepositoryImpl(
  private val userRemoteDataSource: UserRemoteDataSource,
  private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

  override suspend fun getUserProfile(): Flow<ResponseWrapper<User>> {
    return object : FetchDataWrapper<UserResponse, User>() {
      override suspend fun fetchData(): UserResponse {
        return userRemoteDataSource.getUserProfile()
      }

      override suspend fun mapData(response: UserResponse): User {
        return mapToUser(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): Flow<ResponseWrapper<Boolean>> {
    return object :
      UpdateDataWrapper<UserResponse>() {
      override suspend fun doUpdate(): UserResponse {
        return userRemoteDataSource.updateUser(updateUserProfileRequest, image)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<UserResponse>() {
      override suspend fun doUpdate(): UserResponse {
        return userRemoteDataSource.uploadUserVerification(ktp, selfie)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun confirmUserIdentity(
    identityConfirmationRequest: IdentityConfirmationRequest
  ): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<UserResponse>() {
      override suspend fun doUpdate(): UserResponse {
        return userRemoteDataSource.confirmUserIdentity(identityConfirmationRequest)
      }
    }.updateData().flowOn(ioDispatcher)
  }
}