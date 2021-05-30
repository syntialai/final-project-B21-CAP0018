package com.bangkit.team18.qhope.ui.base.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel
import com.bangkit.team18.qhope.utils.SnackbarUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class BaseActivityViewModel<VB : ViewBinding, VM : BaseViewModel>(
  private val inflater: (LayoutInflater) -> VB, viewModelClazz: KClass<VM>
) : AppCompatActivity(),
  View.OnClickListener {

  private var _binding: VB? = null
  protected val binding get() = _binding as VB

  protected val viewModel: VM by viewModel(viewModelClazz)

  protected lateinit var intentLauncher: ActivityResultLauncher<Intent>
  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = inflater.invoke(layoutInflater)
    setContentView(binding.root)
    setupViews(savedInstanceState)
    setupObserver()
    setupActivityResultLauncher()
  }

  private fun setupActivityResultLauncher() {
    intentLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
          onIntentResult(result.data)
        }
      }
    requestPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          onPermissionGranted()
        } else {
          onPermissionNotGranted()
        }
      }
  }

  private fun requestPermission() {


  }

  abstract fun setupViews(savedInstanceState: Bundle?)

  open fun setupObserver() {
    viewModel.fetchStatus.observe(this, {
      it?.let { response ->
        checkErrorState(response)
        showLoadingState(response is ResponseWrapper.Loading)
      }
    })
  }

  open fun showEmptyState(isEmpty: Boolean) {}

  open fun showLoadingState(isLoading: Boolean) {}

  protected fun showErrorToast(message: String?, defaultMessageId: Int) {
    Toast.makeText(binding.root.context, message ?: getString(defaultMessageId), Toast.LENGTH_SHORT)
      .show()
  }

  protected fun showToast(messageId: Int) {
    SnackbarUtils.showSnackbar(binding.root, getString(messageId))
  }

  private fun checkErrorState(wrapper: ResponseWrapper<*>) {
    when (wrapper) {
      is ResponseWrapper.Error -> showErrorToast(wrapper.message, R.string.unknown_error_message)
      is ResponseWrapper.NetworkError -> showErrorToast(null, R.string.no_connection_message)
      else -> {
      }
    }
  }

  protected fun checkPermission(permission: String) {
    if (ContextCompat.checkSelfPermission(
        this,
        permission
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      onPermissionGranted()
    } else {
      requestPermissionLauncher.launch(permission)
    }
  }

  open fun onPermissionGranted() {}
  open fun onPermissionNotGranted() {}
  open fun onIntentResult(data: Intent?) {}
}