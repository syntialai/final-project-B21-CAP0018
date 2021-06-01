package com.bangkit.team18.qhope.ui.registration.view

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityIdVerificationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.IdVerificationViewModel
import com.bangkit.team18.qhope.utils.Router

class IdVerificationActivity :
  BaseActivityViewModel<ActivityIdVerificationBinding, IdVerificationViewModel>(
    ActivityIdVerificationBinding::inflate,
    IdVerificationViewModel::class
  ) {
  enum class DocumentType {
    KTP, SELFIE
  }

  override fun setupViews(savedInstanceState: Bundle?) {
    binding.apply {
      setupListener()
      viewModel.ktpPicture.observe(this@IdVerificationActivity, {
        setupImageBitmap(idVerificationKtpPicture, it)
        idVerificationEditKtpPicture.isVisible = it.isNotNull()
        idVerificationDeleteKtpPicture.isVisible = it.isNotNull()
      })
      viewModel.selfiePicture.observe(this@IdVerificationActivity, {
        setupImageBitmap(idVerificationSelfiePicture, it)
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
  }

  private fun checkSubmitButton() {
    binding.idVerificationSubmit.isEnabled =
      viewModel.ktpPicture.value.isNotNull() && viewModel.selfiePicture.value.isNotNull()
  }

  private fun setupImageBitmap(imageView: ImageView, bitmap: Bitmap?) {
    if (bitmap.isNotNull()) {
      imageView.apply {
        setColorFilter(0xFFFFFFFF.toInt(), PorterDuff.Mode.MULTIPLY)
        setImageBitmap(bitmap)
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
    }
  }

  override fun onPermissionGranted() {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intentLauncher.launch(cameraIntent)
  }

  private fun openCamera(documentType: DocumentType) {
    viewModel.setDocumentType(documentType)
    checkPermission(CAMERA)
  }

  override fun onIntentResult(data: Intent?) {
    data?.extras?.get("data")?.let {
      val bitmap = it as Bitmap
      viewModel.setDocument(bitmap)
    }
  }
}