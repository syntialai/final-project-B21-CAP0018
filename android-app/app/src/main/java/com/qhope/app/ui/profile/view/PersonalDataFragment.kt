package com.qhope.app.ui.profile.view

import android.view.View
import com.qhope.app.databinding.FragmentPersonalDataBinding
import com.qhope.app.ui.base.view.BaseFragment
import com.qhope.app.ui.profile.viewmodel.PersonalDataViewModel
import com.qhope.core.domain.model.user.GenderType
import com.qhope.core.domain.model.user.User
import com.qhope.core.utils.view.DataUtils.orHyphen
import com.qhope.core.utils.view.DateUtils.toDateString

class PersonalDataFragment : BaseFragment<FragmentPersonalDataBinding, PersonalDataViewModel>(
  FragmentPersonalDataBinding::inflate,
  PersonalDataViewModel::class
) {

  override fun setupViews() {
    // No Implementation Needed
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
        personalDataBirthDate.text = user.birthDate?.toDateString("dd MMMM yyyy", true).orHyphen()
        personalDataPlaceOfBirth.text = user.placeOfBirth.orHyphen()
        personalDataAddress.text = user.address.orHyphen()
        personalDataGender.text = user.gender?.name ?: GenderType.MALE.name
      }
    }
  }

  override fun onClick(v: View?) {
  }
}