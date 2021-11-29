package com.bangkit.team18.qhope.ui.profile.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.DocumentType
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.FileUtil
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentProfileIdVerificationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileIdVerificationViewModel
import java.io.File

class ProfileIdVerificationFragment :
  BaseFragment<FragmentProfileIdVerificationBinding, ProfileIdVerificationViewModel>(
    FragmentProfileIdVerificationBinding::inflate,
    ProfileIdVerificationViewModel::class
  ) {

  override fun setupViews() {
    setupListener()
    binding.apply {
      setupListener()
      viewModel.ktpPicture.observe(viewLifecycleOwner, {
        setupImage(profileIdVerificationKtpPicture, it)
        profileIdVerificationEditKtpPicture.isVisible = it.isNotNull()
        profileIdVerificationDeleteKtpPicture.isVisible = it.isNotNull()
        checkSubmitButton()
      })
      viewModel.selfiePicture.observe(viewLifecycleOwner, {
        setupImage(profileIdVerificationSelfiePicture, it)
        profileIdVerificationEditSelfiePicture.isVisible = it.isNotNull()
        profileIdVerificationDeleteSelfiePicture.isVisible = it.isNotNull()
        checkSubmitButton()
      })
    }
  }

  override fun setupObserver() {
    super.setupObserver()
    viewModel.getUserDoc()

    viewModel.isSubmitted.observe(viewLifecycleOwner, {
      if (it) {
        findNavController().navigate(ProfileIdVerificationFragmentDirections.actionProfileIdVerificationFragmentToProfileVerificationResultFragment())
      }
    })
  }

  private fun checkSubmitButton() {
    binding.profileIdVerificationSubmit.isEnabled =
      viewModel.ktpPicture.value.isNotNull() && viewModel.selfiePicture.value.isNotNull()
  }

  private fun setupImage(imageView: ImageView, file: File?) {
    if (file.isNotNull()) {
      imageView.apply {
        setColorFilter(0xFFFFFFFF.toInt(), PorterDuff.Mode.MULTIPLY)
        imageView.loadImage(mContext, file as File)
        setOnClickListener(null)
      }
    } else {
      imageView.apply {
        setColorFilter(mContext.getColor(R.color.grey_300))
        setImageResource(R.drawable.ic_add)
        setOnClickListener(this@ProfileIdVerificationFragment)
      }
    }
  }

  private fun setupListener() {
    binding.apply {
      profileIdVerificationKtpPicture.setOnClickListener(this@ProfileIdVerificationFragment)
      profileIdVerificationSelfiePicture.setOnClickListener(this@ProfileIdVerificationFragment)
      profileIdVerificationEditKtpPicture.setOnClickListener(this@ProfileIdVerificationFragment)
      profileIdVerificationEditSelfiePicture.setOnClickListener(this@ProfileIdVerificationFragment)
      profileIdVerificationDeleteKtpPicture.setOnClickListener(this@ProfileIdVerificationFragment)
      profileIdVerificationDeleteSelfiePicture.setOnClickListener(this@ProfileIdVerificationFragment)
      profileIdVerificationSubmit.setOnClickListener(this@ProfileIdVerificationFragment)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.profile_id_verification_ktp_picture -> openCamera(DocumentType.KTP)
      R.id.profile_id_verification_selfie_picture -> openCamera(DocumentType.SELFIE)
      R.id.profile_id_verification_edit_ktp_picture -> openCamera(DocumentType.KTP)
      R.id.profile_id_verification_edit_selfie_picture -> openCamera(DocumentType.SELFIE)
      R.id.profile_id_verification_delete_ktp_picture -> viewModel.clearDocument(DocumentType.KTP)
      R.id.profile_id_verification_delete_selfie_picture -> viewModel.clearDocument(DocumentType.SELFIE)
      R.id.profile_id_verification_submit -> viewModel.upload()
    }
  }

  override fun onPermissionGrantedChange(isGranted: Boolean) {
    if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
      checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    } else if (!isPermissionGranted(Manifest.permission.CAMERA)) {
      checkPermission(Manifest.permission.CAMERA)
    } else {
      viewModel.getTemporaryDocumentFile()?.let {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
          putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(mContext, it))
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
    viewModel.setTemporaryFile(FileUtil.createImageFile(mContext))
    checkPermission(Manifest.permission.CAMERA)
  }

  override fun onResultWithoutData(result: ActivityResult?) {
    if (result?.data != null) {
      result.data?.data?.let { uri ->
        FileUtil.getFileAbsolutePath(mContext.contentResolver, uri)?.let {
          viewModel.setDocument(mContext, File(it))
        }
      }
    } else if (result?.resultCode == Activity.RESULT_OK) {
      viewModel.setDocument(mContext)
    }
  }
}