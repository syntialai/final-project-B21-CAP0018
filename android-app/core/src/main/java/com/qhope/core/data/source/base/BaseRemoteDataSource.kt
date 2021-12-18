package com.qhope.core.data.source.base

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import java.io.IOException

@Suppress("RemoveExplicitTypeArguments")
@ExperimentalCoroutinesApi
abstract class BaseRemoteDataSource {

  protected fun <T> Flow<T>.transformToResponse(): Flow<ResponseWrapper<T>> {
    return transform { value ->
      emit(ResponseWrapper.Success(value) as ResponseWrapper<T>)
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

  protected fun Task<AuthResult>.loadData(): Flow<ResponseWrapper<FirebaseUser>> = callbackFlow {
    offer(ResponseWrapper.Loading())
    addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        close(task.exception)
      } else {
        offer(ResponseWrapper.Success(task.result.user as FirebaseUser))
      }
    }
    awaitClose { }
  }
}