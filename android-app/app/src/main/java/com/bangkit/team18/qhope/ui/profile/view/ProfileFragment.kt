package com.bangkit.team18.qhope.ui.profile.view

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.core.utils.view.ViewUtils.getColorFromAttr
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
    binding.apply {
      profileLogOut.setOnClickListener(this@ProfileFragment)
      profileIdVerification.setOnClickListener(this@ProfileFragment)
      profilePersonalData.setOnClickListener(this@ProfileFragment)
      profilePictureImage.setOnClickListener(this@ProfileFragment)
    }
  }

  override fun setupObserver() {
    super.setupObserver()
    viewModel.getUserDoc()

    viewModel.userDoc.observe(viewLifecycleOwner, {
      setupUserProfile(it)
    })
  }

  private fun setupUserProfile(user: User) {
    binding.apply {
      profileName.text = user.name
      profilePictureImage.loadImage(mContext, user.imageUrl, R.drawable.default_profile_picture)

      val statusText: String
      val textColorInt: Int
      when (user.verificationStatus) {
        VerificationStatus.VERIFIED -> {
          statusText = getString(R.string.verified_label)
          textColorInt = mContext.getColorFromAttr(R.attr.colorSecondaryVariant)
        }
        VerificationStatus.ACCEPTED -> {
          statusText = getString(R.string.accepted_label)
          textColorInt = mContext.getColorFromAttr(R.attr.colorSecondaryVariant)
        }
        else -> {
          statusText = getString(R.string.not_verified_label)
          textColorInt = ResourcesCompat.getColor(mContext.resources, R.color.grey_300, null)
        }
      }

      profileVerificationStatus.apply {
        text = statusText
        setTextColor(textColorInt)
        chipIconTint = ColorStateList.valueOf(textColorInt)
      }
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
      R.id.profile_personal_data -> findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPersonalDataFragment())
      R.id.profile_log_out -> viewModel.logOut()
      R.id.profile_picture_image -> findNavController().navigate(
        ProfileFragmentDirections.actionProfileFragmentToProfilePictureFragment(
          viewModel.userDoc.value?.imageUrl
        )
      )
    }
  }
}