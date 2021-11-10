package com.bangkit.team18.core.data.source.impl

import android.net.Uri
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.service.UserService
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.user.UserResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@ExperimentalCoroutinesApi
class UserRemoteDataSourceImpl(
  database: FirebaseFirestore,
  firebaseStorage: FirebaseStorage,
  private val userService: UserService
) : UserRemoteDataSource, BaseRemoteDataSource() {
  private val storageReference = firebaseStorage.reference
  private val usersCollection = database.collection(CollectionConstants.USERS_COLLECTION)

  override suspend fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>> {
    val imageReference = storageReference
      .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${CollectionConstants.PROFILE_STORAGE_PATH}/${imageUri.lastPathSegment}")
    return imageReference.addFile(imageUri).transformToResponse()
  }

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

  override suspend fun getUser(userId: String): Flow<UserResponse?> {
    return usersCollection.document(userId).loadData(UserResponse::class.java)
  }

  override suspend fun uploadUserKtp(userId: String, uri: Uri): Flow<ResponseWrapper<Uri>> {
    val imageReference = storageReference
      .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${CollectionConstants.KTP_STORAGE_PATH}/ktp_$userId.jpg")
    return imageReference.addFile(uri).transformToResponse()
  }

  override suspend fun uploadUserSelfie(
    userId: String,
    uri: Uri
  ): Flow<ResponseWrapper<Uri>> {
    val imageReference = storageReference
      .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${CollectionConstants.SELFIE_STORAGE_PATH}/selfie_$userId.jpg")
    return imageReference.addFile(uri).transformToResponse()
  }
}

