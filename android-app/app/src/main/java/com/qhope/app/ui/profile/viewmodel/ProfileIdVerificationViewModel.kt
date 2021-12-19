package com.qhope.app.ui.profile.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.user.DocumentType
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.model.user.VerificationStatus
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.UserUseCase
import com.qhope.core.utils.view.DataUtils.isNotNull
import id.zelory.compressor.Compressor
import java.io.File

class ProfileIdVerificationViewModel(
  authSharedPrefRepository: AuthSharedPrefRepository,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var documentType: DocumentType = DocumentType.KTP

  private var ktpFile: File? = null

  private var selfieFile: File? = null

  private val _ktpPicture = MutableLiveData<File?>()
  val ktpPicture: LiveData<File?> get() = _ktpPicture

  private val _selfiePicture = MutableLiveData<File?>()
  val selfiePicture: LiveData<File?> get() = _selfiePicture

  private val _submitStatus = MutableLiveData<Pair<Boolean, VerificationStatus?>>()
  val submitStatus: LiveData<Pair<Boolean, VerificationStatus?>> get() = _submitStatus

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  fun getUserDoc() {
    launchViewModelScope({
      userUseCase.getUserProfile().runFlow({
        _userDoc.value = it
      })
    })
  }

  fun setDocumentType(documentType: DocumentType) {
    this.documentType = documentType
  }

  fun setTemporaryFile(file: File) {
    if (documentType == DocumentType.KTP) {
      ktpFile = file
    } else {
      selfieFile = file
    }
  }

  fun setDocument(context: Context, file: File? = null) {
    launchViewModelScope({
      if (documentType == DocumentType.KTP) {
        val imageFile = file ?: ktpFile
        val compressedImageFile =
          if (imageFile != null) Compressor.compress(context, imageFile) else imageFile
        _ktpPicture.value = compressedImageFile
      } else {
        val imageFile = file ?: selfieFile
        val compressedImageFile =
          if (imageFile != null) Compressor.compress(context, imageFile) else imageFile
        _selfiePicture.value = compressedImageFile
      }
    })
  }

  fun clearDocument(documentType: DocumentType) {
    if (documentType == DocumentType.KTP) {
      _ktpPicture.value = null
    } else {
      _selfiePicture.value = null
    }
  }

  fun getTemporaryDocumentFile(): File? {
    return if (documentType == DocumentType.KTP) {
      ktpFile
    } else {
      selfieFile
    }
  }

  fun upload() {
    val ktp = _ktpPicture.value
    val selfie = _selfiePicture.value
    if (ktp != null && selfie != null) {
      launchViewModelScope({
        userUseCase.uploadUserVerification(ktp, selfie).runFlow({ user ->
          _submitStatus.value = Pair(user.verificationStatus.isNotNull(), user.verificationStatus)
        }, {
          _submitStatus.value = Pair(false, null)
        })
      })
    }
  }
}