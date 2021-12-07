package com.bangkit.team18.qhope.ui.profile.view

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.utils.view.DateUtils.toDateString
import com.bangkit.team18.core.utils.view.PickerUtils
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentEditPersonalDataBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.EditPersonalDataViewModel
import com.bangkit.team18.qhope.ui.registration.view.RegistrationActivity
import com.google.android.material.datepicker.MaterialDatePicker

class EditPersonalDataFragment :
  BaseFragment<FragmentEditPersonalDataBinding, EditPersonalDataViewModel>(
    FragmentEditPersonalDataBinding::inflate,
    EditPersonalDataViewModel::class
  ) {

  private var birthDatePicker: MaterialDatePicker<Long> =
    PickerUtils.getDatePicker(R.string.birth_date_hint)

  override fun setupViews() {
    binding.apply {
      editPersonalDataName.doOnTextChanged { _, _, _, _ ->
        editPersonalDataName.error = null
      }
      editPersonalDataGenderMale.setOnClickListener(this@EditPersonalDataFragment)
      editPersonalDataGenderFemale.setOnClickListener(this@EditPersonalDataFragment)
    }
    birthDatePicker.addOnPositiveButtonClickListener {
      viewModel.setBirthDate(it / 1000)
    }

    setHasOptionsMenu(true)
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.getUserDoc()

    viewModel.userDoc.observe(viewLifecycleOwner, {
      setupPersonalData(it)
    })
    viewModel.birthDate.observe(viewLifecycleOwner, {
      binding.editPersonalDataBirthDate.setText(
        it.toDateString("yyyy-MM-dd", true)
      )
    })
    viewModel.saved.observe(viewLifecycleOwner, {
      if (it) {
        findNavController().navigateUp()
      }
    })
  }


  private fun setupPersonalData(user: User?) {
    if (user != null) {
      binding.apply {
        editPersonalDataName.setText(user.name)
        editPersonalDataKtpNumber.setText(user.ktpNumber)
        editPersonalDataBirthDate.setOnClickListener(this@EditPersonalDataFragment)
        editPersonalDataPlaceOfBirth.setText(user.placeOfBirth)
        editPersonalDataAddress.setText(user.address)
        user.birthDate?.let {
          birthDatePicker = PickerUtils.getDatePicker(R.string.birth_date_hint, it)
          viewModel.setBirthDate(it / 1000)
        }
        editPersonalDataGenderMale.isChecked = user.gender == GenderType.MALE
        editPersonalDataGenderFemale.isChecked = user.gender == GenderType.FEMALE
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.edit_personal_data_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.edit_personal_data_done_item -> {
        validate()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun validate() {
    var valid = true
    binding.apply {
      if (editPersonalDataName.length() == 0) {
        valid = false
        editPersonalDataName.error = getString(R.string.name_field_error)
      }
      if (!editPersonalDataGenderMale.isChecked && !editPersonalDataGenderFemale.isChecked) {
        valid = false
        editPersonalDataGenderMale.error = getString(R.string.gender_field_error)
        editPersonalDataGenderFemale.error = getString(R.string.gender_field_error)
      }
      if (valid) {
        val name = editPersonalDataName.text.toString()
        val placeOfBirth = editPersonalDataPlaceOfBirth.text.toString()
        val address = editPersonalDataAddress.text.toString()
        val gender =
          if (editPersonalDataGenderMale.isChecked) GenderType.MALE else GenderType.FEMALE
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
      R.id.edit_personal_data_gender_male -> clearGenderError()
      R.id.edit_personal_data_gender_female -> clearGenderError()
    }
  }

  private fun clearGenderError() {
    binding.apply {
      editPersonalDataGenderMale.error = null
      editPersonalDataGenderFemale.error = null
    }
  }
}