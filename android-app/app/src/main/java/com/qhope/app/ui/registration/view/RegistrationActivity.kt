package com.qhope.app.ui.registration.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.MaterialDatePicker
import com.qhope.app.R
import com.qhope.app.databinding.ActivityRegistrationBinding
import com.qhope.app.ui.base.view.BaseActivityViewModel
import com.qhope.app.ui.registration.viewmodel.RegistrationViewModel
import com.qhope.app.utils.Router
import com.qhope.core.utils.view.DateUtils.toDateString
import com.qhope.core.utils.view.FileUtil
import com.qhope.core.utils.view.ViewUtils.loadImage
import java.io.File

class RegistrationActivity :
  BaseActivityViewModel<ActivityRegistrationBinding, RegistrationViewModel>(
    ActivityRegistrationBinding::inflate,
    RegistrationViewModel::class
  ) {

  companion object {
    const val TAG = ".ui.registration.view.RegistrationActivity"
  }

  private val birthDatePicker: MaterialDatePicker<Long> =
    MaterialDatePicker.Builder.datePicker().setTitleText(
      R.string.birth_date_hint
    ).build()

  override fun setupViews(savedInstanceState: Bundle?) {
    supportActionBar?.hide()
    binding.apply {
      registrationLogOut.setOnClickListener(this@RegistrationActivity)
      registrationBirthDate.setOnClickListener(this@RegistrationActivity)
      registrationEditProfilePicture.setOnClickListener(this@RegistrationActivity)
      bNext.setOnClickListener(this@RegistrationActivity)
      registrationName.doOnTextChanged { _, _, _, _ ->
        validateInput()
      }
      registrationBirthDate.doOnTextChanged { _, _, _, _ ->
        validateInput()
      }
    }
    setupDatePicker()
    viewModel.profilePicture.observe(this, {
      binding.registrationProfilePicture.loadImage<File>(this, it)
    })
    viewModel.birthDate.observe(this, {
      binding.registrationBirthDate.setText(
        it.toDateString("yyyy-MM-dd", true)
      )
    })
    viewModel.isSubmitted.observe(this, {
      if (it) {
        Router.goToIdVerification(this)
      }
    })
  }

  private fun setupDatePicker() {
    birthDatePicker.addOnPositiveButtonClickListener {
      viewModel.setBirthDate(it / 1000)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.registration_log_out -> viewModel.logOut()
      R.id.registration_birth_date -> {
        if (birthDatePicker.isAdded.not()) {
          birthDatePicker.show(this.supportFragmentManager, TAG)
        }
      }
      R.id.registration_edit_profile_picture -> checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
      R.id.b_next -> {
        if (validateInput()) {
          viewModel.submitData(binding.registrationName.text.toString())
        }
      }
    }
  }

  private fun validateInput(): Boolean {
    var valid = true
    binding.apply {
      if (registrationName.length() == 0) {
        registrationName.error = getString(R.string.name_field_error)
        valid = false
      }
      if (registrationBirthDate.length() == 0) {
        registrationBirthDate.error = getString(R.string.birth_date_field_error)
        valid = false
      }
      bNext.isEnabled = valid
    }
    return valid
  }

  override fun onPermissionsGranted() {
    openGallery()
  }

  private fun openGallery() {
    val openGalleryIntent = Intent(
      Intent.ACTION_PICK,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    ).apply {
      type = "image/*"
    }
    intentLauncher.launch(openGalleryIntent)
  }

  override fun onIntentResult(data: Intent?) {
    data?.data?.let { uri ->
      FileUtil.getFileAbsolutePath(this.contentResolver, uri)?.let {
        viewModel.setProfilePicture(it)
      }
    }
  }
}