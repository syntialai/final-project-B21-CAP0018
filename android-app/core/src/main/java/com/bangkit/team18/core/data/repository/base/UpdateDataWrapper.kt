package com.bangkit.team18.core.data.repository.base

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class UpdateDataWrapper<Response> {

  protected abstract suspend fun doUpdate(): Response

  suspend fun updateData(): Flow<ResponseWrapper<Boolean>> {
    return flow {
      emit(ResponseWrapper.Loading())
      try {
        doUpdate()
        emit(ResponseWrapper.Success(true))
      } catch (exception: Exception) {
        emit((if (isNetworkError(exception)) {
          ResponseWrapper.NetworkError()
        } else {
          ResponseWrapper.Error<Boolean>(exception.message)
        }))
      }
    }
  }

  private fun isNetworkError(throwable: Throwable) = throwable is IOException
}