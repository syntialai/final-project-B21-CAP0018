package com.bangkit.team18.core.data.repository.base

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import retrofit2.HttpException
import java.io.IOException

abstract class FetchDataWrapper<Response, Model> {

  protected abstract suspend fun fetchData(): Flow<Response?>

  protected abstract suspend fun mapData(response: Response): Model

  suspend fun getData(): Flow<ResponseWrapper<Model>> {
    return fetchData().transform<Response?, ResponseWrapper<Model>> { value ->
      value?.let {
        val model = mapData(it)
        emit(ResponseWrapper.Success(model))
      }
    }.onStart {
      emit(ResponseWrapper.Loading())
    }.catch { exception ->
      emit(
        when {
          isNetworkError(exception) -> ResponseWrapper.NetworkError()
          isHttpError(exception) -> {
            val httpException = exception as HttpException
            ResponseWrapper.HttpError(httpException.code(), httpException.message())
          }
          else -> ResponseWrapper.Error(exception.message)
        }
      )
    }
  }

  suspend fun updateData(): Flow<ResponseWrapper<Model>> {
    return fetchData().transform<Response?, ResponseWrapper<Model>> { value ->
      value?.let {
        emit(ResponseWrapper.Success(mapData(it)))
      }
    }.onStart {
      emit(ResponseWrapper.Loading())
    }.catch { exception ->
      emit(
        when {
          isNetworkError(exception) -> ResponseWrapper.NetworkError()
          isHttpError(exception) -> {
            val httpException = exception as HttpException
            ResponseWrapper.HttpError(httpException.code(), httpException.message())
          }
          else -> ResponseWrapper.Error(exception.message)
        }
      )
    }
  }

  private fun isNetworkError(throwable: Throwable) = throwable is IOException

  private fun isHttpError(throwable: Throwable) = throwable is HttpException
}