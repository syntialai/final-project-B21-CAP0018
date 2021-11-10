package com.bangkit.team18.qhope.ui.profile.view

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.DataUtils.orHyphen
import com.bangkit.team18.core.utils.view.DateUtils.toDateString
import com.bangkit.team18.core.utils.view.FileUtil
import com.bangkit.team18.core.utils.view.PickerUtils.getDatePicker
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentPersonalDataBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.PersonalDataViewModel
import com.bangkit.team18.qhope.ui.registration.view.RegistrationActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File

class PersonalDataFragment : BaseFragment<FragmentPersonalDataBinding, PersonalDataViewModel>(
  FragmentPersonalDataBinding::inflate,
  PersonalDataViewModel::class
) {
  private var birthDatePicker: MaterialDatePicker<Long> = getDatePicker(R.string.birth_date_hint)

  private var menu: Menu? = null

  override fun setupViews() {
    binding.apply {
      personalDataName.doOnTextChanged { _, _, _, _ ->
        personalDataName.error = null
      }
      personalDataGenderMale.setOnClickListener(this@PersonalDataFragment)
      personalDataGenderFemale.setOnClickListener(this@PersonalDataFragment)
      personalDataProfilePicture.setOnClickListener(this@PersonalDataFragment)
    }
    birthDatePicker.addOnPositiveButtonClickListener {
      viewModel.setBirthDate(it / 1000)
    }

    setHasOptionsMenu(true)
  }

  override fun setupObserver() {
    super.setupObserver()
    viewModel.user.observe(viewLifecycleOwner, {
      if (it.isNotNull()) {
        viewModel.getUserDoc()
      }
    })
    viewModel.userDoc.observe(viewLifecycleOwner, {
      setupPersonalData(viewModel.mode.value as PersonalDataViewModel.ModeType, it)
    })
    viewModel.mode.observe(viewLifecycleOwner, {
      setupPersonalData(it, viewModel.userDoc.value)
      setupMenuItem(it)
    })
    viewModel.birthDate.observe(this, {
      binding.personalDataBirthDate.setText(
        it.toDateString("yyyy-MM-dd", true)
      )
    })
    viewModel.profilePicture.observe(this, {
      binding.personalDataProfilePicture.loadImage<File>(mContext, it)
    })
  }

  private fun setupMenuItem(mode: PersonalDataViewModel.ModeType) {
    menu?.apply {
      findItem(R.id.personal_data_edit_item)?.isVisible = !getIsEditable(mode)
      findItem(R.id.personal_data_close_item)?.isVisible = getIsEditable(mode)
      findItem(R.id.personal_data_done_item)?.isVisible = getIsEditable(mode)
    }
  }

  private fun setupPersonalData(mode: PersonalDataViewModel.ModeType, user: User?) {
    if (user != null) {
      binding.apply {
        if (mode == PersonalDataViewModel.ModeType.VIEW) {
          personalDataName.setText(user.name.orHyphen())
          personalDataKtpNumber.setText(user.ktpNumber.orHyphen())
          personalDataBirthDate.setOnClickListener(null)
          personalDataPlaceOfBirth.setText(user.placeOfBirth.orHyphen())
          personalDataAddress.setText(user.address.orHyphen())
        } else {
          personalDataName.setText(user.name)
          personalDataKtpNumber.setText(user.ktpNumber)
          personalDataBirthDate.setOnClickListener(this@PersonalDataFragment)
          personalDataPlaceOfBirth.setText(user.placeOfBirth)
          personalDataAddress.setText(user.address)
        }
        personalDataProfilePicture.loadImage(mContext, user.imageUrl, R.drawable.ic_person)
        val isEditable = getIsEditable(mode)
        user.birthDate?.let {
          birthDatePicker = getDatePicker(R.string.birth_date_hint, it)
        }
        personalDataName.apply {
          isFocusable = isEditable
          isFocusableInTouchMode = isEditable
          isCursorVisible = isEditable
        }
        personalDataKtpNumber.apply {
          isFocusable = isEditable
          isFocusableInTouchMode = isEditable
          isCursorVisible = isEditable
        }
        personalDataPlaceOfBirth.apply {
          isFocusable = isEditable
          isFocusableInTouchMode = isEditable
          isCursorVisible = isEditable
        }
        personalDataAddress.apply {
          isFocusable = isEditable
          isFocusableInTouchMode = isEditable
          isCursorVisible = isEditable
        }
        personalDataGenderMale.isClickable = isEditable
        personalDataGenderMale.isChecked = user.gender == GenderType.MALE
        personalDataGenderFemale.isClickable = isEditable
        personalDataGenderFemale.isChecked = user.gender == GenderType.FEMALE
        personalDataProfilePicture.isClickable = isEditable

        user.birthDate?.let {
          viewModel.setBirthDate(it / 1000)
        }
      }
    }
  }

  private fun getIsEditable(mode: PersonalDataViewModel.ModeType): Boolean {
    return mode != PersonalDataViewModel.ModeType.VIEW
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.personal_data_menu, menu)
    this.menu = menu
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.personal_data_edit_item -> viewModel.changeMode()
      R.id.personal_data_close_item -> viewModel.changeMode()
      R.id.personal_data_done_item -> {
        validate()
      }
      R.id.registration_edit_profile_picture -> openGallery()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onPermissionGrantedChange(isGranted: Boolean) {
    if (isGranted) {
      val openGalleryIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
      ).apply {
        type = "image/*"
      }
      intentLauncher.launch(openGalleryIntent)
    }
  }

  private fun openGallery() {
    checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
  }

  override fun onIntentResult(data: Intent?) {
    data?.data?.let { uri ->
      FileUtil.getFileAbsolutePath(mContext.contentResolver, uri)?.let {
        viewModel.setProfilePicture(it)
      }
    }
  }

  private fun validate() {
    var valid = true
    binding.apply {
      if (personalDataName.length() == 0) {
        valid = false
        personalDataName.error = getString(R.string.name_field_error)
      }
      if (!personalDataGenderMale.isChecked && !personalDataGenderFemale.isChecked) {
        valid = false
        personalDataGenderMale.error = getString(R.string.gender_field_error)
        personalDataGenderFemale.error = getString(R.string.gender_field_error)
      }
      if (valid) {
        val name = personalDataName.text.toString()
        val ktpNumber = personalDataKtpNumber.text.toString()
        val placeOfBirth = personalDataPlaceOfBirth.text.toString()
        val address = personalDataAddress.text.toString()
        val gender = if (personalDataGenderMale.isChecked) GenderType.MALE else GenderType.FEMALE
        viewModel.update(name, placeOfBirth, address, gender)
      }
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.personal_data_birth_date -> {
        if (birthDatePicker.isAdded.not()) {
          birthDatePicker.show(childFragmentManager, RegistrationActivity.TAG)
        }
      }
      R.id.personal_data_gender_male -> clearGenderError()
      R.id.personal_data_gender_female -> clearGenderError()
      R.id.personal_data_profile_picture -> openGallery()
    }
  }

  private fun clearGenderError() {
    binding.apply {
      personalDataGenderMale.error = null
      personalDataGenderFemale.error = null
    }
  }
}