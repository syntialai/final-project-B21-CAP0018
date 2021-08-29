package com.bangkit.team18.qhope.ui.profile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.DocumentType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import id.zelory.compressor.Compressor
import java.io.File

class ProfileIdVerificationViewModel(
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) :
  BaseViewModelWithAuth(authUseCase) {
  private var documentType: DocumentType =
    DocumentType.KTP
  private var ktpFile: File? = null
  private var selfieFile: File? = null
  private val _ktpPicture = MutableLiveData<File?>()
  val ktpPicture: LiveData<File?> get() = _ktpPicture
  private val _selfiePicture = MutableLiveData<File?>()
  val selfiePicture: LiveData<File?> get() = _selfiePicture
  private val _isSubmitted = MutableLiveData<Boolean>()
  val isSubmitted: LiveData<Boolean> get() = _isSubmitted
  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  init {
    initAuthStateListener()
  }

  fun getUserDoc() {
    launchViewModelScope({
      getUserId()?.let { uid ->
        userUseCase.getUser(uid).runFlow({
          it?.let { user ->
            _userDoc.value = user
          }
        })
      }
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
    val ktp = Uri.fromFile(ktpPicture.value)
    val selfie = Uri.fromFile(selfiePicture.value)
    getUserId()?.let { id ->
      launchViewModelScope({
        userUseCase.uploadUserKtp(id, ktp).runFlow({ ktpUri ->
          launchViewModelScope({
            userUseCase.uploadUserSelfie(id, selfie).runFlow({ selfieUri ->
              launchViewModelScope({
                userUseCase.updateUserVerification(id, ktpUri.toString(), selfieUri.toString())
                  .runFlow({
                    _isSubmitted.value = it
                  })
              })
            })
          })
        })
      })
    }
  }
}