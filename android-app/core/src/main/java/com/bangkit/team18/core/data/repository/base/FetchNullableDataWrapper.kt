package com.bangkit.team18.core.data.repository.base

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import java.io.IOException

abstract class FetchNullableDataWrapper<Response, Model> {

  protected abstract suspend fun fetchData(): Flow<Response?>

  protected abstract suspend fun mapData(response: Response?): Model?

  suspend fun getData(): Flow<ResponseWrapper<Model?>> {
    return fetchData().transform<Response?, ResponseWrapper<Model?>> { value ->
      if (value != null) {
        val model = mapData(value)
        emit(ResponseWrapper.Success(model))
      } else {
        emit(ResponseWrapper.Success(null))
      }
    }.onStart {
      emit(ResponseWrapper.Loading())
    }.catch { exception ->
      emit(
        if (isNetworkError(exception)) {
          ResponseWrapper.NetworkError()
        } else {
          ResponseWrapper.Error(exception.message)
        }
      )
    }
  }

  private fun isNetworkError(throwable: Throwable) = throwable is IOException
}