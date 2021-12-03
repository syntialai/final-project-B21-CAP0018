package com.bangkit.team18.qhope.ui.profile.view

import android.view.View
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentProfileVerificationResultBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileVerificationResultViewModel

class ProfileVerificationResultFragment :
  BaseFragment<FragmentProfileVerificationResultBinding, ProfileVerificationResultViewModel>(
    FragmentProfileVerificationResultBinding::inflate,
    ProfileVerificationResultViewModel::class
  ) {

  override fun setupViews() {
    binding.apply {
      profileVerificationResultFinish.setOnClickListener(this@ProfileVerificationResultFragment)
    }
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.getUserDoc()

    viewModel.userDoc.observe(viewLifecycleOwner, {
      binding.apply {
        when (it.verificationStatus) {
          VerificationStatus.VERIFIED, VerificationStatus.ACCEPTED -> {
            profileVerificationResultMessage.text = getString(R.string.verified_message)
            profileVerificationResultMessageDescription.text =
              getString(R.string.verified_message_description)
            profileVerificationResultAnimation.setAnimation("verified.json")
          }
          VerificationStatus.REJECTED -> {
            profileVerificationResultMessage.text = getString(R.string.rejected_message)
            profileVerificationResultMessageDescription.text =
              getString(R.string.rejected_message_description)
            profileVerificationResultAnimation.setAnimation("rejected.json")
          }
          else -> {
            profileVerificationResultMessage.text = getString(R.string.uploaded_message)
            profileVerificationResultMessageDescription.text =
              getString(R.string.uploaded_message_description)
            profileVerificationResultAnimation.setAnimation("uploaded.json")
          }
        }
      }
    })
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.profile_verification_result_finish -> {
        viewModel.userDoc.value?.let {
          if (it.verificationStatus == VerificationStatus.REJECTED) {
            findNavController().navigate(ProfileVerificationResultFragmentDirections.actionProfileVerificationResultFragmentToProfileIdVerificationFragment())
          } else {
            findNavController().navigateUp()
          }
        }
      }
    }
  }
}