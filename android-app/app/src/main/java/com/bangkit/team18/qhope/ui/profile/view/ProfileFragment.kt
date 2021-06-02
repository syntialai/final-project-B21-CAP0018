package com.bangkit.team18.qhope.ui.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentProfileBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
  FragmentProfileBinding::inflate,
  ProfileViewModel::class
) {
  override fun setupViews() {
    setupListener()
    viewModel.user.observe(viewLifecycleOwner, {
      if (it.isNotNull()) {
        viewModel.getUserDoc()
      }
    })
    viewModel.userDoc.observe(viewLifecycleOwner, {
      setupUserProfile(it)
    })
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
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
      R.id.profile_id_verification -> {
        viewModel.userDoc.value?.let {
          if (it.verificationStatus == VerificationStatus.NOT_UPLOAD) {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileIdVerificationFragment())
          } else {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileVerificationResultFragment2())
          }
        }
      }
      R.id.profile_log_out -> viewModel.logOut()
    }
  }
}