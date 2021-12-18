package com.qhope.core.data.repository

import com.qhope.core.data.source.request.user.IdentityConfirmationRequest
import com.qhope.core.data.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.source.response.user.UserResponse
import com.qhope.core.data.mapper.UserMapper
import com.qhope.core.data.repository.base.FetchDataWrapper
import com.qhope.core.data.repository.base.UpdateDataWrapper
import com.qhope.core.data.source.UserRemoteDataSource
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.repository.UserRepository
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

  override suspend fun getUserProfile(maskNik: Boolean): Flow<ResponseWrapper<User>> {
    return object : FetchDataWrapper<UserResponse, User>() {
      override suspend fun fetchData(): UserResponse {
        return userRemoteDataSource.getUserProfile()
      }

      override suspend fun mapData(response: UserResponse): User {
        return UserMapper.mapToUser(response, maskNik)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): Flow<ResponseWrapper<User>> {
    return object : FetchDataWrapper<UserResponse, User>() {
      override suspend fun fetchData(): UserResponse {
        return userRemoteDataSource.updateUser(updateUserProfileRequest, image)
      }

      override suspend fun mapData(response: UserResponse): User {
        return UserMapper.mapToUser(response)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): Flow<ResponseWrapper<User>> {
    return object : FetchDataWrapper<UserResponse, User>() {
      override suspend fun fetchData(): UserResponse {
        return userRemoteDataSource.uploadUserVerification(ktp, selfie)
      }

      override suspend fun mapData(response: UserResponse): User {
        return UserMapper.mapToUser(response)
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