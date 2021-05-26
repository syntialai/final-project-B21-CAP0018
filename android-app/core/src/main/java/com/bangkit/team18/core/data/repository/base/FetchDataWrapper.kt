package com.bangkit.team18.core.data.repository.base

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

abstract class FetchDataWrapper<Response, Model> {

  protected abstract fun fetchData(): Flow<Response?>

  protected abstract fun mapData(response: Response): Model

  suspend fun getData(): Flow<ResponseWrapper<Model>> {
    return flow {
      fetchData().onStart {
        emit(ResponseWrapper.Loading<Model>())
      }.catch { exception ->
        emit(if (isNetworkError(exception)) {
          ResponseWrapper.NetworkError<Model>()
        } else {
          ResponseWrapper.Error(exception.message)
        })
      }.collectLatest { response ->
        response?.let {
          val model = mapData(it)
          emit(ResponseWrapper.Success(model))
        }
      }
    }
  }

  private fun isNetworkError(throwable: Throwable) = throwable is IOException
}