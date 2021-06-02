package com.bangkit.team18.qhope.ui.registration.view

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.core.view.isVisible
import com.bangkit.team18.core.domain.model.user.DocumentType
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.core.utils.view.FileUtil
import com.bangkit.team18.core.utils.view.FileUtil.getUri
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityIdVerificationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.IdVerificationViewModel
import com.bangkit.team18.qhope.utils.Router
import java.io.File
import java.util.*

class IdVerificationActivity :
  BaseActivityViewModel<ActivityIdVerificationBinding, IdVerificationViewModel>(
    ActivityIdVerificationBinding::inflate,
    IdVerificationViewModel::class
  ) {

  override fun setupViews(savedInstanceState: Bundle?) {
    supportActionBar?.hide()
    binding.apply {
      setupListener()
      viewModel.ktpPicture.observe(this@IdVerificationActivity, {
        setupImage(idVerificationKtpPicture, it)
        idVerificationEditKtpPicture.isVisible = it.isNotNull()
        idVerificationDeleteKtpPicture.isVisible = it.isNotNull()
      })
      viewModel.selfiePicture.observe(this@IdVerificationActivity, {
        setupImage(idVerificationSelfiePicture, it)
        idVerificationEditSelfiePicture.isVisible = it.isNotNull()
        idVerificationDeleteSelfiePicture.isVisible = it.isNotNull()
        checkSubmitButton()
      })
    }
    viewModel.user.observe(this, {
      if (it.isNull()) {
        Router.goToLogin(this)
      }
    })
    viewModel.isSubmitted.observe(this, {
      if (it) {
        Router.goToVerificationResult(this)
      }
    })
  }

  private fun checkSubmitButton() {
    binding.idVerificationSubmit.isEnabled =
      viewModel.ktpPicture.value.isNotNull() && viewModel.selfiePicture.value.isNotNull()
  }

  private fun setupImage(imageView: ImageView, file: File?) {
    if (file.isNotNull()) {
      imageView.apply {
        setColorFilter(0xFFFFFFFF.toInt(), PorterDuff.Mode.MULTIPLY)
        imageView.loadImage(this@IdVerificationActivity, file as File)
        setOnClickListener(null)
      }
    } else {
      imageView.apply {
        setColorFilter(getColor(R.color.grey_300))
        setImageResource(R.drawable.ic_add)
        setOnClickListener(this@IdVerificationActivity)
      }
    }
  }

  private fun setupListener() {
    binding.apply {
      idVerificationKtpPicture.setOnClickListener(this@IdVerificationActivity)
      idVerificationSelfiePicture.setOnClickListener(this@IdVerificationActivity)
      idVerificationEditKtpPicture.setOnClickListener(this@IdVerificationActivity)
      idVerificationEditSelfiePicture.setOnClickListener(this@IdVerificationActivity)
      idVerificationDeleteKtpPicture.setOnClickListener(this@IdVerificationActivity)
      idVerificationDeleteSelfiePicture.setOnClickListener(this@IdVerificationActivity)
      idVerificationSkip.setOnClickListener(this@IdVerificationActivity)
      idVerificationSubmit.setOnClickListener(this@IdVerificationActivity)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.id_verification_ktp_picture -> openCamera(DocumentType.KTP)
      R.id.id_verification_selfie_picture -> openCamera(DocumentType.SELFIE)
      R.id.id_verification_edit_ktp_picture -> openCamera(DocumentType.KTP)
      R.id.id_verification_edit_selfie_picture -> openCamera(DocumentType.SELFIE)
      R.id.id_verification_delete_ktp_picture -> viewModel.clearDocument(DocumentType.KTP)
      R.id.id_verification_delete_selfie_picture -> viewModel.clearDocument(DocumentType.SELFIE)
      R.id.id_verification_skip -> Router.goToMain(this)
      R.id.id_verification_submit -> viewModel.upload()
    }
  }

  override fun onPermissionGranted() {
    viewModel.getTemporaryDocumentFile()?.let {
      val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, getUri(this@IdVerificationActivity, it))
      }
      intentLauncher.launch(cameraIntent)
    }
  }

  private fun openCamera(documentType: DocumentType) {
    viewModel.setDocumentType(documentType)
    viewModel.setTemporaryFile(FileUtil.createImageFile(this))
    checkPermission(CAMERA)
  }

  override fun onResultWithoutData(result: ActivityResult?) {
    if (result?.resultCode == Activity.RESULT_OK) {
      viewModel.setDocument()
    }
  }
}