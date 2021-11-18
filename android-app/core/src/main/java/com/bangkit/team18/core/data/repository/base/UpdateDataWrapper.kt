package com.bangkit.team18.core.data.repository.base

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

@Suppress("RemoveExplicitTypeArguments")
abstract class UpdateDataWrapper<Response> {

  protected abstract suspend fun doUpdate(): Response

  suspend fun updateData(): Flow<ResponseWrapper<Boolean>> {
    return flow {
      emit(ResponseWrapper.Loading<Boolean>())
      try {
        doUpdate()
        emit(ResponseWrapper.Success(true))
      } catch (throwable: Throwable) {
        emit(
          when {
            isNetworkError(throwable) -> ResponseWrapper.NetworkError<Boolean>()
            isHttpError(throwable) -> {
              val httpException = throwable as HttpException
              ResponseWrapper.HttpError(httpException.code(), httpException.message())
            }
            else -> ResponseWrapper.Error(throwable.message)
          }
        )
      }
    }
  }

  private fun isNetworkError(throwable: Throwable) = throwable is IOException

  private fun isHttpError(throwable: Throwable) = throwable is HttpException
}