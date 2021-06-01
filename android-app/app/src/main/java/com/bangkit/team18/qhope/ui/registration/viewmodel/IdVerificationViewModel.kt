package com.bangkit.team18.qhope.ui.registration.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.bangkit.team18.qhope.ui.registration.view.IdVerificationActivity.DocumentType

class IdVerificationViewModel(
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authUseCase) {
  private var documentType: DocumentType = DocumentType.KTP
  private val _ktpPicture = MutableLiveData<Bitmap?>()
  val ktpPicture: LiveData<Bitmap?> get() = _ktpPicture
  private val _selfiePicture = MutableLiveData<Bitmap?>()
  val selfiePicture: LiveData<Bitmap?> get() = _selfiePicture
  private val _isSubmitted = MutableLiveData<Boolean>()
  val isSubmitted: LiveData<Boolean> get() = _isSubmitted
  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  init {
    initAuthStateListener()
    user.value?.let {
      launchViewModelScope({
        userUseCase.getUser(it.uid).runFlow({
          _userDoc.value = it
        })
      })
    }
  }

  fun setDocumentType(documentType: DocumentType) {
    this.documentType = documentType
  }

  fun setDocument(bitmap: Bitmap) {
    if (documentType == DocumentType.KTP) {
      _ktpPicture.postValue(bitmap)
    } else {
      _selfiePicture.postValue(bitmap)
    }
  }

  fun clearDocument(documentType: DocumentType) {
    if (documentType == DocumentType.KTP) {
      _ktpPicture.postValue(null)
    } else {
      _selfiePicture.postValue(null)
    }
  }
}