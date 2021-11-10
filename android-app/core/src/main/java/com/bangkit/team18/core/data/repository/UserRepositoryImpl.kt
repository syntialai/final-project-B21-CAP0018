package com.bangkit.team18.core.data.repository

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.mapper.UserMapper
import com.bangkit.team18.core.data.mapper.UserMapper.mapToUser
import com.bangkit.team18.core.data.repository.base.FetchNullableDataWrapper
import com.bangkit.team18.core.data.repository.base.UpdateDataWrapper
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.response.user.UserResponse
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

  override suspend fun addUser(userId: String, user: User): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<Unit>() {
      override suspend fun doUpdate() {
        val data = hashMapOf<String, Any?>(
          UserMapper.NAME_FIELD to user.name,
          UserMapper.PHONE_NUMBER_FIELD to user.phoneNumber,
          UserMapper.IMAGE_URL_FIELD to user.imageUrl,
          UserMapper.BIRTH_DATE_FIELD to user.birthDate,
          UserMapper.VERIFICATION_STATUS_FIELD to user.verificationStatus
        )
        userRemoteDataSource.updateUser(userId, data)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun getUser(userId: String): Flow<ResponseWrapper<User?>> {
    return object : FetchNullableDataWrapper<UserResponse, User>() {
      override suspend fun fetchData(): Flow<UserResponse?> {
        return userRemoteDataSource.getUser(userId)
      }

      override suspend fun mapData(response: UserResponse?): User? {
        return response?.mapToUser()
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): Flow<ResponseWrapper<Boolean>> {
    return object :
      UpdateDataWrapper<com.bangkit.team18.core.api.source.response.user.UserResponse>() {
      override suspend fun doUpdate(): com.bangkit.team18.core.api.source.response.user.UserResponse {
        return userRemoteDataSource.updateUser(updateUserProfileRequest, image)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): Flow<ResponseWrapper<Boolean>> {
    return object :
      UpdateDataWrapper<com.bangkit.team18.core.api.source.response.user.UserResponse>() {
      override suspend fun doUpdate(): com.bangkit.team18.core.api.source.response.user.UserResponse {
        return userRemoteDataSource.uploadUserVerification(ktp, selfie)
      }
    }.updateData().flowOn(ioDispatcher)
  }
}