package com.bangkit.team18.qhope.ui.registration.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.DocumentType
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import java.io.File

class IdVerificationViewModel(
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authUseCase) {
  private var documentType: DocumentType = DocumentType.KTP
  private var ktpFile: File? = null
  private var selfieFile: File? = null
  private val _ktpPicture = MutableLiveData<File?>()
  val ktpPicture: LiveData<File?> get() = _ktpPicture
  private val _selfiePicture = MutableLiveData<File?>()
  val selfiePicture: LiveData<File?> get() = _selfiePicture
  private val _isSubmitted = MutableLiveData<Boolean>()
  val isSubmitted: LiveData<Boolean> get() = _isSubmitted

  init {
    initAuthStateListener()
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

  fun setDocument() {
    if (documentType == DocumentType.KTP) {
      _ktpPicture.value = ktpFile
    } else {
      _selfiePicture.value = selfieFile
    }
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