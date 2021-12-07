package com.bangkit.team18.qhope.ui.profile.view

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.utils.view.DataUtils.orHyphen
import com.bangkit.team18.core.utils.view.DateUtils.toDateString
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentPersonalDataBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.PersonalDataViewModel

class PersonalDataFragment : BaseFragment<FragmentPersonalDataBinding, PersonalDataViewModel>(
  FragmentPersonalDataBinding::inflate,
  PersonalDataViewModel::class
) {

  private var menu: Menu? = null

  override fun setupViews() {
    binding.apply {
      personalDataName.doOnTextChanged { _, _, _, _ ->
        personalDataName.error = null
      }
    }

    setHasOptionsMenu(true)
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.getUserDoc()

    viewModel.userDoc.observe(viewLifecycleOwner, this::setupPersonalData)
  }

  private fun setupPersonalData(user: User?) {
    if (user != null) {
      binding.apply {
        personalDataName.text = user.name.orHyphen()
        personalDataKtpNumber.text = user.ktpNumber.orHyphen()
        personalDataBirthDate.text = user.birthDate?.toDateString("dd MMMM yyyy").orHyphen()
        personalDataPlaceOfBirth.text = user.placeOfBirth.orHyphen()
        personalDataAddress.text = user.address.orHyphen()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.personal_data_menu, menu)
    this.menu = menu
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.personal_data_edit_item -> findNavController().navigate(R.id.action_personalDataFragment_to_editPersonalDataFragment)
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onClick(v: View?) {
  }
}