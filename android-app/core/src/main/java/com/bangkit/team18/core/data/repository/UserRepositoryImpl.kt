package com.bangkit.team18.core.data.repository

import android.net.Uri
import com.bangkit.team18.core.data.mapper.UserMapper.mapToUser
import com.bangkit.team18.core.data.mapper.UserMapper.mapToUserResponse
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
        userRemoteDataSource.updateUser(userId, user.mapToUserResponse())
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
}