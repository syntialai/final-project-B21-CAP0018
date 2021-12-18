package com.qhope.app.ui.registration.view

import android.os.Bundle
import android.view.View
import com.qhope.app.R
import com.qhope.app.databinding.ActivityVerificationResultBinding
import com.qhope.app.ui.base.view.BaseActivityViewModel
import com.qhope.app.ui.registration.viewmodel.VerificationResultViewModel
import com.qhope.app.utils.Router
import com.qhope.core.domain.model.user.VerificationStatus

class VerificationResultActivity :
  BaseActivityViewModel<ActivityVerificationResultBinding, VerificationResultViewModel>(
    ActivityVerificationResultBinding::inflate,
    VerificationResultViewModel::class
  ) {

  override fun setupViews(savedInstanceState: Bundle?) {
    supportActionBar?.hide()
    setupListener()
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.getUserDoc()

    viewModel.userDoc.observe(this, {
      binding.apply {
        when (it.verificationStatus) {
          VerificationStatus.VERIFIED, VerificationStatus.ACCEPTED -> {
            verificationResultMessage.text = getString(R.string.verified_message)
            verificationResultMessageDescription.text =
              getString(R.string.verified_message_description)
            verificationResultAnimation.setAnimation("verified.json")
          }
          VerificationStatus.REJECTED -> {
            verificationResultMessage.text = getString(R.string.rejected_message)
            verificationResultMessageDescription.text =
              getString(R.string.rejected_message_description)
            verificationResultAnimation.setAnimation("rejected.json")
          }
          else -> {
            verificationResultMessage.text = getString(R.string.uploaded_message)
            verificationResultMessageDescription.text =
              getString(R.string.uploaded_message_description)
            verificationResultAnimation.setAnimation("uploaded.json")
          }
        }
      }
    })
  }

  private fun setupListener() {
    binding.verificationResultFinish.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.verification_result_finish -> {
        if (viewModel.userDoc.value?.verificationStatus == VerificationStatus.ACCEPTED) {
          Router.goToIdentityConfirmation(this)
        } else {
          Router.goToMain(this)
        }
      }
    }
  }
}