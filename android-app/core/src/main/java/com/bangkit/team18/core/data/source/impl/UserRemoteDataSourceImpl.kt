package com.bangkit.team18.core.data.source.impl

import android.net.Uri
import com.bangkit.team18.core.data.mapper.UserMapper
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.user.UserResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import java.io.IOException

@ExperimentalCoroutinesApi
class UserRemoteDataSourceImpl(
  database: FirebaseFirestore,
  firebaseStorage: FirebaseStorage
) : UserRemoteDataSource, BaseRemoteDataSource() {
  private val storageReference = firebaseStorage.reference
  private val usersCollection = database.collection(CollectionConstants.USERS_COLLECTION)

  override suspend fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>> {
    val imageReference = storageReference
      .child("${CollectionConstants.IMAGES_USERS_STORAGE_PATH}/$userId/${CollectionConstants.PROFILE_STORAGE_PATH}/${imageUri.lastPathSegment}")
    return imageReference.addFile(imageUri).transform { value ->
      emit(ResponseWrapper.Success(value) as ResponseWrapper<Uri>)
    }.onStart {
      emit(ResponseWrapper.Loading())
    }.catch { exception ->
      if (exception is IOException) {
        emit(ResponseWrapper.NetworkError())
      } else {
        emit(ResponseWrapper.Error(exception.message))
      }
    }
  }

  override suspend fun updateUser(userId: String, user: UserResponse) {
    usersCollection.updateData(
      userId, hashMapOf(
        UserMapper.NAME_FIELD to user.name,
        UserMapper.PHONE_NUMBER_FIELD to user.phone_number,
        UserMapper.IMAGE_URL_FIELD to user.image_url,
        UserMapper.BIRTH_DATE_FIELD to user.birth_date,
        UserMapper.VERIFICATION_STATUS_FIELD to user.verification_status
      )
    )
  }

  override suspend fun getUser(userId: String): Flow<UserResponse?> {
    return usersCollection.document(userId).loadData(UserResponse::class.java)
  }
}