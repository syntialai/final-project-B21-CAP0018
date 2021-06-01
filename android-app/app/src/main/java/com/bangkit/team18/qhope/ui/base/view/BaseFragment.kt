package com.bangkit.team18.qhope.ui.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel
import com.bangkit.team18.qhope.utils.SnackbarUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
  private val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
  viewModelClazz: KClass<VM>
) : Fragment(), View.OnClickListener {

  private var _binding: VB? = null
  protected val binding get() = _binding as VB

  protected val viewModel: VM by viewModel(viewModelClazz)

  protected lateinit var mContext: Context

  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  protected lateinit var intentLauncher: ActivityResultLauncher<Intent>

  protected var lifecycleJob: Job? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context
    intentLauncher = registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        onIntentResult(result.data)
      }
    }
    requestPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted ->
      onPermissionGrantedChange(isGranted)
    }
  }

  open fun onIntentResult(data: Intent?) {}

  open fun onPermissionGrantedChange(isGranted: Boolean) {}

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = viewBindingInflater.invoke(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupViews()
    setupObserver()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  abstract fun setupViews()

  open fun setupObserver() {
    viewModel.fetchStatus.observe(viewLifecycleOwner, {
      it?.let { response ->
        checkErrorState(response)
        showLoadingState(response is ResponseWrapper.Loading)
      }
    })
  }

  open fun showEmptyState(isEmpty: Boolean) {}

  open fun showLoadingState(isLoading: Boolean) {}

  protected fun checkPermission(permission: String) {
    if (checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
      onPermissionGrantedChange(true)
    } else {
      requestPermissionLauncher.launch(permission)
    }
  }

  protected fun showErrorToast(message: String? = null, defaultMessageId: Int) {
    Toast.makeText(binding.root.context, message ?: getString(defaultMessageId), Toast.LENGTH_SHORT)
      .show()
  }

  protected fun showToast(messageId: Int) {
    SnackbarUtils.showSnackbar(binding.root, getString(messageId))
  }

  protected fun launchJob(block: () -> Unit) {
    lifecycleJob?.cancel()
    lifecycleJob = lifecycleScope.launch {
      delay(500)
      block.invoke()
    }
  }

  private fun checkErrorState(wrapper: ResponseWrapper<*>) {
    when (wrapper) {
      is ResponseWrapper.Error -> showErrorToast(wrapper.message, R.string.unknown_error_message)
      is ResponseWrapper.NetworkError -> showErrorToast(null, R.string.no_connection_message)
      else -> {
      }
    }
  }
}