package com.bangkit.team18.qhope.ui.base.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bangkit.team18.qhope.utils.view.SnackbarUtils

abstract class BaseFragment<VB : ViewBinding>(
  private val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment(),
  View.OnClickListener {

  private var _binding: VB? = null
  protected val binding get() = _binding as VB

  protected lateinit var mContext: Context

  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  protected lateinit var intentLauncher: ActivityResultLauncher<Intent>

  override fun onAttach(context: Context) {
    super.onAttach(context)
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

  open fun setupObserver() {}

  protected fun checkPermission(permission: String) {
    if (checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
      onPermissionGrantedChange(true)
    } else {
      requestPermissionLauncher.launch(permission)
    }
  }

  protected fun showErrorToast(messageId: Int) {
    SnackbarUtils.showErrorSnackbar(binding.root, getString(messageId))
  }

  protected fun showToast(messageId: Int) {
    SnackbarUtils.showSnackbar(binding.root, getString(messageId))
  }
}