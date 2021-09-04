package com.bangkit.team18.core.data.source.response.wrapper

sealed class ResponseWrapper<T> {

  data class Success<T>(val data: T) : ResponseWrapper<T>()

  data class Loading<T>(val data: T? = null) : ResponseWrapper<T>()

  data class Error<T>(val message: String? = null) : ResponseWrapper<T>()

  data class NetworkError<T>(val message: String? = null) : ResponseWrapper<T>()
}