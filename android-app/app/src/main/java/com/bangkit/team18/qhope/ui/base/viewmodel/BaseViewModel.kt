package com.bangkit.team18.qhope.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

  protected fun launchViewModelScope(block: suspend () -> Unit,
      dispatcher: CoroutineDispatcher = Dispatchers.Main) {
    viewModelScope.launch(dispatcher) {
      block.invoke()
    }
  }

  protected suspend fun <T> Flow<T>.runFlow() {
    onStart {
      startLoading()
    }.collectLatest { data ->
      checkResponse(data)
    }
  }

  private fun <T> checkResponse(data: T) {
    // TODO: Implement after data wrapper done
  }

  private fun startLoading() {
    // TODO: Implement after data wrapper done
  }
}