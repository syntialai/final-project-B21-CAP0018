package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.service.UserService
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@ExperimentalCoroutinesApi
class UserRemoteDataSourceImpl(
  database: FirebaseFirestore,
  private val userService: UserService
) : UserRemoteDataSource, BaseRemoteDataSource() {

  private val usersCollection = database.collection(CollectionConstants.USERS_COLLECTION)

  override suspend fun updateUser(userId: String, data: HashMap<String, Any?>) {
    usersCollection.updateData(userId, data)
  }

  override suspend fun updateUser(
    userProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): com.bangkit.team18.core.api.source.response.user.UserResponse {
    val fileBody = image?.asRequestBody("image/*".toMediaTypeOrNull())
    val filePart = fileBody?.let {
      MultipartBody.Part.createFormData("image", image.name.orEmpty(), it)
    }
    return userService.updateUserProfile(userProfileRequest, filePart)
  }

  override suspend fun getUserProfile(): Flow<com.bangkit.team18.core.api.source.response.user.UserResponse> {
    return userService.getUserProfile().loadAsFlow()
  }

  override suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): com.bangkit.team18.core.api.source.response.user.UserResponse {
    val ktpFileBody = ktp.asRequestBody("image/*".toMediaTypeOrNull())
    val ktpFilePart = MultipartBody.Part.createFormData("ktp", ktp.name.orEmpty(), ktpFileBody)

    val selfieFileBody = selfie.asRequestBody("image/*".toMediaTypeOrNull())
    val selfieFilePart =
      MultipartBody.Part.createFormData("selfie", selfie.name.orEmpty(), selfieFileBody)

    return userService.updateUserProfile(UpdateUserProfileRequest(), ktpFilePart, selfieFilePart)
  }
}

