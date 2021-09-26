package com.bangkit.team18.core.data.repository

import android.net.Uri
import com.bangkit.team18.core.data.mapper.UserMapper
import com.bangkit.team18.core.data.mapper.UserMapper.mapToUser
import com.bangkit.team18.core.data.repository.base.FetchNullableDataWrapper
import com.bangkit.team18.core.data.repository.base.UpdateDataWrapper
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.response.user.UserResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class UserRepositoryImpl(
  private val userRemoteDataSource: UserRemoteDataSource,
  private val ioDispatcher: CoroutineDispatcher
) : UserRepository {
  override suspend fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>> =
    userRemoteDataSource.uploadUserImage(userId, imageUri)

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

  override suspend fun uploadUserKtp(userId: String, uri: Uri): Flow<ResponseWrapper<Uri>> =
    userRemoteDataSource.uploadUserKtp(userId, uri)

  override suspend fun uploadUserSelfie(
    userId: String,
    uri: Uri
  ): Flow<ResponseWrapper<Uri>> = userRemoteDataSource.uploadUserSelfie(userId, uri)

  override suspend fun updateUserVerification(
    userId: String,
    ktpUrl: String,
    selfieUrl: String
  ): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<Unit>() {
      override suspend fun doUpdate() {
        val data = hashMapOf<String, Any?>(
          UserMapper.VERIFICATION_STATUS_FIELD to VerificationStatus.UPLOADED,
          UserMapper.KTP_URL_FIELD to ktpUrl,
          UserMapper.SELFIE_URL_FIELD to selfieUrl,
        )
        userRemoteDataSource.updateUser(userId, data)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun updatePersonalData(
    userId: String,
    user: User
  ): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<Unit>() {
      override suspend fun doUpdate() {
        val data = hashMapOf<String, Any?>(
          UserMapper.NAME_FIELD to user.name,
          UserMapper.IMAGE_URL_FIELD to user.imageUrl,
          UserMapper.BIRTH_DATE_FIELD to user.birthDate,
          UserMapper.ADDRESS_FIELD to user.address,
          UserMapper.PLACE_OF_BIRTH_FIELD to user.placeOfBirth,
          UserMapper.GENDER_FIELD to user.gender,
          UserMapper.NO_KTP_FIELD to user.ktpNumber
        )
        userRemoteDataSource.updateUser(userId, data)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun updateProfilePicture(
    userId: String,
    imageUrl: String
  ): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<Unit>() {
      override suspend fun doUpdate() {
        val data = hashMapOf<String, Any?>(
          UserMapper.IMAGE_URL_FIELD to imageUrl,
        )
        userRemoteDataSource.updateUser(userId, data)
      }
    }.updateData().flowOn(ioDispatcher)
  }
}