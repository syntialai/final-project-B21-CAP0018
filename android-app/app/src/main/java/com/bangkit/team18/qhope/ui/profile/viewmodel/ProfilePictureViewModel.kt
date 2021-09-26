package com.bangkit.team18.qhope.ui.profile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import id.zelory.compressor.Compressor
import java.io.File

class ProfilePictureViewModel(authUseCase: AuthUseCase, private val userUseCase: UserUseCase) :
  BaseViewModelWithAuth(authUseCase) {

  private var _profilePicture = MutableLiveData<File>()
  val profilePicture: LiveData<File> get() = _profilePicture

  private var _saved = MutableLiveData<Boolean>()
  val saved: LiveData<Boolean> get() = _saved

  init {
    initAuthStateListener()
  }

  fun setProfilePicture(context: Context, filePath: String) {
    launchViewModelScope({
      _profilePicture.value = Compressor.compress(context, File(filePath))
    })
  }

  fun save() {
    launchViewModelScope({
      getUserId()?.let { userId ->
        profilePicture.value?.let {
          val imageUri = Uri.fromFile(_profilePicture.value)
          userUseCase.uploadUserImage(userId, imageUri).runFlow({ uri ->
            launchViewModelScope({
              userUseCase.updateProfilePicture(userId, uri.toString()).runFlow({
                _saved.value = it
              })
            })
          })
        }
      }
    })
  }
}