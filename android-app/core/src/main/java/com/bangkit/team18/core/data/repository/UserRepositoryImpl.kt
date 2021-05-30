package com.bangkit.team18.core.data.repository

import android.net.Uri
import com.bangkit.team18.core.data.mapper.UserMapper.BIRTH_DATE_FIELD
import com.bangkit.team18.core.data.mapper.UserMapper.IMAGE_URL_FIELD
import com.bangkit.team18.core.data.mapper.UserMapper.NAME_FIELD
import com.bangkit.team18.core.data.mapper.UserMapper.PHONE_NUMBER_FIELD
import com.bangkit.team18.core.data.source.config.CollectionConstants.IMAGES_USERS_STORAGE_PATH
import com.bangkit.team18.core.data.source.config.CollectionConstants.PROFILE_STORAGE_PATH
import com.bangkit.team18.core.data.source.config.CollectionConstants.USERS_COLLECTION
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.repository.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class UserRepositoryImpl(
  private val database: FirebaseFirestore,
  private val firebaseStorage: FirebaseStorage
) : UserRepository {
  private val storageReference = firebaseStorage.reference
  private val usersCollection = database.collection(USERS_COLLECTION)

  override fun uploadUserImage(userId: String, imageUri: Uri) = callbackFlow {
    val imageReference = storageReference
      .child("$IMAGES_USERS_STORAGE_PATH/$userId/$PROFILE_STORAGE_PATH/${imageUri.lastPathSegment}")
    val uploadImageTask = imageReference.putFile(imageUri)
    val onCompleteListener = OnCompleteListener<Uri> { task ->
      task.result?.let {
        trySend(ResponseWrapper.Success(it))
      } ?: run {
        trySend(ResponseWrapper.Error<Uri>(task.exception?.message))
      }
    }
    uploadImageTask.continueWithTask { task ->
      if (task.isSuccessful.not()) {
        offer(ResponseWrapper.Error<Uri>(task.exception?.message))
      }
      imageReference.downloadUrl
    }.addOnCompleteListener(onCompleteListener)
    awaitClose { }
  }

  override fun addUser(userId: String, user: User) = flow {
    val task = usersCollection.document(userId).set(
      hashMapOf(
        NAME_FIELD to user.name,
        PHONE_NUMBER_FIELD to user.phoneNumber,
        IMAGE_URL_FIELD to user.imageUrl,
        BIRTH_DATE_FIELD to user.birthDate
      )
    )
    try {
      task.await()
      emit(ResponseWrapper.Success(true))
    } catch (e: Exception) {
      emit(ResponseWrapper.Error<Boolean>(e.message))
    }
  }
}