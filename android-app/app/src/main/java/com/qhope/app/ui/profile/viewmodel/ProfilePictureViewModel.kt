package com.qhope.app.ui.profile.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.UserUseCase
import com.qhope.core.utils.view.DataUtils.isNotNull
import id.zelory.compressor.Compressor
import java.io.File

class ProfilePictureViewModel(
  authSharedPrefRepository: AuthSharedPrefRepository,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _profilePicture = MutableLiveData<File>()
  val profilePicture: LiveData<File> get() = _profilePicture

  private var _saved = MutableLiveData<Boolean>()
  val saved: LiveData<Boolean> get() = _saved

  init {
    initAuthStateListener()
  }

  fun setProfilePicture(context: Context, filePath: String) {
    launchViewModelScope({
      Compressor.compress(context, File(filePath))?.let {
        _profilePicture.value = it
      }
    })
  }

  fun save() {
    launchViewModelScope({
      profilePicture.value?.let {
        userUseCase.updateUser(UpdateUserProfileRequest(), it).runFlow({ user ->
          _saved.value = user.isNotNull()
        })
      }
    })
  }
}