package com.bangkit.team18.qhope.ui.profile.view

import android.view.View
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentProfileBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
  FragmentProfileBinding::inflate,
  ProfileViewModel::class
) {
  companion object {
    fun newInstance() = ProfileFragment()
  }

  override fun setupViews() {
    setupListener()
    viewModel.userDoc.observe(viewLifecycleOwner, {
      setupUserProfile(it)
    })
  }

  private fun setupListener() {
    binding.apply {
      profileLogOut.setOnClickListener(this@ProfileFragment)
      profileIdVerification.setOnClickListener(this@ProfileFragment)
      profilePersonalData.setOnClickListener(this@ProfileFragment)
    }
  }

  private fun setupUserProfile(user: User) {
    binding.apply {
      profileName.text = user.name
      profilePictureImage.loadImage(mContext, user.imageUrl, R.drawable.ic_person)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.profile_log_out -> {
        viewModel.logOut()
      }
    }
  }
}