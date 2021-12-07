package com.bangkit.team18.qhope.ui.base.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.utils.view.DialogUtils
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel
import com.bangkit.team18.qhope.utils.Router
import com.bangkit.team18.qhope.utils.PermissionUtil
import com.bangkit.team18.qhope.utils.SnackbarUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.HttpURLConnection
import kotlin.reflect.KClass


abstract class BaseActivityViewModel<VB : ViewBinding, VM : BaseViewModel>(
  private val inflater: (LayoutInflater) -> VB, viewModelClazz: KClass<VM>
) : AppCompatActivity(),
  View.OnClickListener {

  private var _binding: VB? = null
  protected val binding get() = _binding as VB

  protected val viewModel: VM by viewModel(viewModelClazz)

  protected lateinit var intentLauncher: ActivityResultLauncher<Intent>

  private var loadingDialog: Dialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = inflater.invoke(layoutInflater)
    setContentView(binding.root)
    setupViews(savedInstanceState)
    setupLoadingDialog()
    setupObserver()
    setupActivityResultLauncher()
  }

  private fun setupActivityResultLauncher() {
    intentLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
          onResultWithoutData(result)
          onIntentResult(result.data)
        }
      }
  }

  abstract fun setupViews(savedInstanceState: Bundle?)

  open fun setupObserver() {
    viewModelWithAuth = viewModel as? BaseViewModelWithAuth

    viewModel.fetchStatus.observe(this, {
      it?.let { response ->
        checkErrorState(response)
        showLoadingState(response is ResponseWrapper.Loading)
      }
    })
    viewModelWithAuth?.loggedOut?.observe(this, { loggedOut ->
      if (loggedOut) {
        viewModelWithAuth?.resetLogOutValue()
        Router.goToLogin(this)
      }
    })
  }

  open fun showEmptyState(isEmpty: Boolean) {}

  private fun setupLoadingDialog() {
    loadingDialog = DialogUtils.createDialog(this, R.layout.layout_loading_dialog)
  }

  open fun showLoadingState(isLoading: Boolean) {
    if (isLoading) {
      DialogUtils.showDialog(loadingDialog)
    } else {
      DialogUtils.dismissDialog(loadingDialog)
    }
  }

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
      is ResponseWrapper.HttpError -> onHttpError(wrapper.code ?: 0, wrapper.message)
      is ResponseWrapper.NetworkError -> showErrorToast(null, R.string.no_connection_message)
      else -> {
      }
    }
  }

  private fun onHttpError(code: Int, message: String?) {
    when (code) {
      HttpURLConnection.HTTP_UNAUTHORIZED -> (viewModel as? BaseViewModelWithAuth)?.logOut()
      else -> showErrorToast(message, R.string.unknown_error_message)
    }
  }

  protected fun checkPermissions(vararg permissions: String) {
    PermissionUtil.checkPermissions(
      this,
      permissions.toList(),
      this::onPermissionsGranted,
      this::onAnyPermissionsDenied
    )
  }

  open fun onAnyPermissionsDenied(permissions: List<String>) {
    PermissionUtil.onAnyPermissionsDenied(binding.root, permissions)
  }

  open fun onPermissionsGranted() {}
  open fun onIntentResult(data: Intent?) {}
  open fun onResultWithoutData(result: ActivityResult?) {}
}