package com.bangkit.team18.qhope.ui.registration.view

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.utils.view.DataUtils.orHyphen
import com.bangkit.team18.core.utils.view.DateUtils.toDateString
import com.bangkit.team18.core.utils.view.PickerUtils
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityIdentityConfirmationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.IdentityConfirmationViewModel
import com.bangkit.team18.qhope.utils.Router
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IdentityConfirmationActivity :
  BaseActivityViewModel<ActivityIdentityConfirmationBinding, IdentityConfirmationViewModel>(
    ActivityIdentityConfirmationBinding::inflate,
    IdentityConfirmationViewModel::class
  ) {

  companion object {
    const val TAG = ".ui.registration.view.IdentityConfirmationActivity"
  }

  private val saveIdentityAlertDialog: AlertDialog by lazy {
    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.confirm_identity_title)
      .setMessage(R.string.confirm_identity_description)
      .setPositiveButton(R.string.save) { dialog, _ ->
        saveIdentity()
        dialog.dismiss()
      }.setNegativeButton(R.string.back) { dialog, _ ->
        dialog.dismiss()
      }.create()
  }

  private val birthDatePicker: MaterialDatePicker<Long> by lazy {
    PickerUtils.getDatePicker(R.string.birth_date_hint)
  }

  override fun setupViews(savedInstanceState: Bundle?) {
    with(binding) {
      etBirthDate.setOnClickListener(this@IdentityConfirmationActivity)
      buttonConfirmIdentity.setOnClickListener(this@IdentityConfirmationActivity)
    }
    birthDatePicker.addOnPositiveButtonClickListener {
      viewModel.setBirthDate(it / 1000)
    }
    setupReligionAutoComplete()
    setupBloodTypeAutoComplete()
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.getUserDoc()
    viewModel.userDoc.observe(this, { user ->
      setupUser(user)
    })
    viewModel.saveEvent.observe(this, { saved ->
      if (saved) {
        Router.goToMain(this)
      }
    })
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        etBirthDate -> openDatePicker()
        buttonConfirmIdentity -> openSaveConfirmationDialog()
      }
    }
  }

  private fun setupReligionAutoComplete() {
    val religions = resources.getStringArray(R.array.religions)
    val religionAdapter =
      ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, religions)
    binding.actvReligion.setAdapter(religionAdapter)
    religionAdapter.notifyDataSetChanged()
  }

  private fun setupBloodTypeAutoComplete() {
    val bloodTypes = resources.getStringArray(R.array.blood_types)
    val bloodTypeAdapter =
      ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, bloodTypes)
    binding.actvBloodType.setAdapter(bloodTypeAdapter)
    bloodTypeAdapter.notifyDataSetChanged()
  }

  private fun saveIdentity() {
    with(binding) {
      viewModel.save(
        etName.text.toString(),
        getGenderType(),
        etPlaceOfBirth.text.toString(),
        etKtpAddress.text.toString(),
        etDistrict.text.toString(),
        etCity.text.toString(),
        etVillage.text.toString(),
        etHamlet.text.toString(),
        actvBloodType.text.toString(),
        actvReligion.text.toString()
      )
    }
  }

  private fun getGenderType(): GenderType {
    return if (binding.rbGenderMale.isChecked) {
      GenderType.MALE
    } else {
      GenderType.FEMALE
    }
  }

  private fun openDatePicker() {
    if (birthDatePicker.isAdded.not()) {
      birthDatePicker.show(supportFragmentManager, TAG)
    }
  }

  private fun openSaveConfirmationDialog() {
    if (saveIdentityAlertDialog.isShowing.not()) {
      saveIdentityAlertDialog.show()
    } else {
      saveIdentityAlertDialog.dismiss()
      openSaveConfirmationDialog()
    }
  }

  private fun setupUser(user: User) {
    with(binding) {
      etKtpNumber.setText(user.ktpNumber.orHyphen())
      etName.setText(user.name.orHyphen())
      etBirthDate.setText(user.birthDate?.toDateString("yyyy-MM-dd", true))
      etKtpAddress.setText(user.address.orHyphen())
      etPlaceOfBirth.setText(user.placeOfBirth.orHyphen())
      rbGenderMale.isChecked = user.gender == GenderType.MALE
      rbGenderFemale.isChecked = user.gender == GenderType.FEMALE
      etDistrict.setText(user.district)
      etCity.setText(user.city)
      etVillage.setText(user.village)
      etHamlet.setText(user.hamlet)
      actvReligion.setText(user.religion)
      actvBloodType.setText(user.bloodType)
    }
  }
}