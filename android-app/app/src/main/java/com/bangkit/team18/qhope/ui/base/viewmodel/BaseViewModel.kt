package com.bangkit.team18.qhope.ui.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
  private var _fetchStatus = MutableLiveData<ResponseWrapper<*>>()
  val fetchStatus: LiveData<ResponseWrapper<*>>
    get() = _fetchStatus

  protected fun launchViewModelScope(
    block: suspend () -> Unit,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
  ) {
    viewModelScope.launch(dispatcher) {
      block.invoke()
    }
  }

  protected suspend fun <T> Flow<ResponseWrapper<T>>.runFlow(
    onSuccessFetch: (T) -> Unit,
    onFailFetch: (() -> Unit)? = null
  ) {
    collect { data ->
      checkResponse(data, onSuccessFetch, onFailFetch)
    }
  }

  protected fun <T> MutableLiveData<T>.publishChanges() {
    this.value = this.value
  }

  private fun <T> checkResponse(
    wrapper: ResponseWrapper<T>, onSuccessFetch: (T) -> Unit,
    onFailFetch: (() -> Unit)?
  ) {
    _fetchStatus.value = wrapper
    when (wrapper) {
      is ResponseWrapper.Success -> onSuccessFetch.invoke(wrapper.data)
      is ResponseWrapper.Loading -> {
      }
      else -> onFailFetch?.invoke()
    }
  }

  protected fun showErrorResponse(message: String) {
    _fetchStatus.value = ResponseWrapper.Error<Any>(message)
  }
}