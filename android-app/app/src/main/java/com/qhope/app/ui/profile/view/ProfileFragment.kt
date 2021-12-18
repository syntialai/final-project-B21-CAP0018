package com.qhope.app.ui.profile.view

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.qhope.app.R
import com.qhope.app.databinding.FragmentProfileBinding
import com.qhope.app.ui.base.view.BaseFragment
import com.qhope.app.ui.profile.viewmodel.ProfileViewModel
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.model.user.VerificationStatus
import com.qhope.core.utils.view.ViewUtils.getColorFromAttr
import com.qhope.core.utils.view.ViewUtils.loadImage

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
      profileTermsAndCondition.setOnClickListener(this@ProfileFragment)
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
            findNavController()
              .navigate(ProfileFragmentDirections.actionProfileFragmentToProfileIdVerificationFragment())
          } else {
            findNavController().navigate(
              ProfileFragmentDirections.actionProfileFragmentToProfileVerificationResultFragment2()
            )
          }
        }
      }
      R.id.profile_personal_data -> findNavController().navigate(
        ProfileFragmentDirections.actionProfileFragmentToPersonalDataFragment()
      )
      R.id.profile_log_out -> viewModelWithAuth?.logOut()
      R.id.profile_picture_image -> findNavController().navigate(
        ProfileFragmentDirections.actionProfileFragmentToProfilePictureFragment(
          viewModel.userDoc.value?.imageUrl
        )
      )
      R.id.profile_terms_and_condition -> findNavController().navigate(
        ProfileFragmentDirections.actionProfileFragmentToTermsAndConditionFragment()
      )
    }
  }
}