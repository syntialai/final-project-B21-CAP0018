package com.bangkit.team18.qhope.ui.registration.view

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import com.bangkit.team18.core.domain.model.user.DocumentType
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.FileUtil
import com.bangkit.team18.core.utils.view.FileUtil.getUri
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
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
    with(binding) {
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

  override fun setupObserver() {
    super.setupObserver()

    viewModel.ktpPicture.observe(this, {
      setupImage(binding.idVerificationKtpPicture, it)
      toggleEditButtonVisibility(false, it.isNotNull())
      checkSubmitButton()
    })

    viewModel.selfiePicture.observe(this, {
      setupImage(binding.idVerificationSelfiePicture, it)
      toggleEditButtonVisibility(true, it.isNotNull())
      checkSubmitButton()
    })

    viewModel.submitStatus.observe(this, { submitStatus ->
      if (submitStatus.first) {
        onSuccessUserVerification(submitStatus.second ?: VerificationStatus.UPLOADED)
      }
    })
  }

  private fun onSuccessUserVerification(verificationStatus: VerificationStatus) {
    if (verificationStatus == VerificationStatus.ACCEPTED) {
      Router.goToIdentityConfirmation(this, true)
    } else {
      Router.goToVerificationResult(this)
    }
  }

  private fun toggleEditButtonVisibility(selfie: Boolean, fileExist: Boolean) {
    with(binding) {
      if (selfie) {
        idVerificationEditSelfiePicture.showOrRemove(fileExist)
        idVerificationDeleteSelfiePicture.showOrRemove(fileExist)
      } else {
        idVerificationEditKtpPicture.showOrRemove(fileExist)
        idVerificationDeleteKtpPicture.showOrRemove(fileExist)
      }
    }
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
    if (!isPermissionGranted(READ_EXTERNAL_STORAGE)) {
      checkPermission(READ_EXTERNAL_STORAGE)
    } else if (!isPermissionGranted(CAMERA)) {
      checkPermission(CAMERA)
    } else {
      viewModel.getTemporaryDocumentFile()?.let {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
          putExtra(MediaStore.EXTRA_OUTPUT, getUri(this@IdVerificationActivity, it))
        }
        val openGalleryIntent = Intent(
          Intent.ACTION_PICK,
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
          type = "image/*"
        }
        val chooserIntent = Intent.createChooser(openGalleryIntent, getString(R.string.intent_choose_capturer)).apply {
          putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        }
        intentLauncher.launch(chooserIntent)
      }
    }
  }

  private fun openCamera(documentType: DocumentType) {
    viewModel.setDocumentType(documentType)
    viewModel.setTemporaryFile(FileUtil.createImageFile(this))
    checkPermission(CAMERA)
  }

  override fun onResultWithoutData(result: ActivityResult?) {
    if (result?.data != null) {
      result.data?.data?.let { uri ->
        FileUtil.getFileAbsolutePath(this.contentResolver, uri)?.let {
          viewModel.setDocument(this, File(it))
        }
      }
    } else if (result?.resultCode == Activity.RESULT_OK) {
      viewModel.setDocument(this)
    }
  }
}