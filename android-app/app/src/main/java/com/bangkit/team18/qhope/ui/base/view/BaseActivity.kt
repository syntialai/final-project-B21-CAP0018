package com.bangkit.team18.qhope.ui.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.bangkit.team18.qhope.utils.SnackbarUtils

abstract class BaseActivity<VB : ViewBinding>(private val inflater: (LayoutInflater) -> VB) :
    AppCompatActivity(), View.OnClickListener {

  private var _binding: VB? = null
  protected val binding get() = _binding as VB

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = inflater.invoke(layoutInflater)
    setContentView(binding.root)
    setupViews(savedInstanceState)
  }

  abstract fun setupViews(savedInstanceState: Bundle?)

  protected fun showErrorToast(message: String?, defaultMessageId: Int) {
    SnackbarUtils.showErrorSnackbar(binding.root, message ?: getString(defaultMessageId))
  }

  protected fun showToast(messageId: Int) {
    SnackbarUtils.showSnackbar(binding.root, getString(messageId))
  }
}