package com.bangkit.team18.qhope.ui.registration.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.core.domain.repository.UserRepository
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import java.io.File

class RegistrationViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModelWithAuth(authRepository) {
    private var _profilePicture = MutableLiveData<File>()
    val profilePicture: LiveData<File> get() = _profilePicture
    private var _isSubmitted = MutableLiveData<Boolean>()
    val isSubmitted: LiveData<Boolean> get() = _isSubmitted
    private var _birthDate = MutableLiveData<Long>()
    val birthDate: LiveData<Long> get() = _birthDate

    init {
        authRepository.addAuthStateListener(this)
    }

    fun setProfilePicture(filePath: String) {
        _profilePicture.postValue(File(filePath))
    }

    fun submitData(name: String) {
        if (profilePicture.value.isNotNull()) {
            val imageUri = Uri.fromFile(_profilePicture.value)
            user.value?.let {
                launchViewModelScope({
                    userRepository.uploadUserImage(it.uid, imageUri).runFlow({ uri ->
                        val user = User(
                            it.uid,
                            name,
                            it.phoneNumber.toString(),
                            uri.toString(),
                            Timestamp(birthDate.value as Long, 0),
                            VerificationStatus.NOT_UPLOAD
                        )
                        submitUser(user)
                    }, {})
                }, Dispatchers.IO)
            }
        }
    }

    private fun submitUser(user: User) {
        launchViewModelScope({
            userRepository.addUser(user.id, user).runFlow({
                _isSubmitted.postValue(it)
            }, {})
        }, Dispatchers.IO)
    }

    fun setBirthDate(birthDate: Long) {
        _birthDate.postValue(birthDate)
    }
}