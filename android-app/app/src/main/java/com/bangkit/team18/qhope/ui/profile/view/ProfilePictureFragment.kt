package com.bangkit.team18.qhope.ui.profile.view

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bangkit.team18.core.utils.view.FileUtil
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentProfilePictureBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfilePictureViewModel
import java.io.File

class ProfilePictureFragment : BaseFragment<FragmentProfilePictureBinding, ProfilePictureViewModel>(
  FragmentProfilePictureBinding::inflate,
  ProfilePictureViewModel::class
) {

  private val args by navArgs<ProfilePictureFragmentArgs>()

  private var menu: Menu? = null

  override fun setupViews() {
    (mContext as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    binding.profilePictureImage.loadImage(
      mContext,
      args.photoUrl,
      R.drawable.default_profile_picture
    )
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.profile_picture_menu, menu)
    this.menu = menu
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.profilePicture.observe(viewLifecycleOwner, {
      binding.profilePictureImage.loadImage<File>(mContext, it)
      changeOptionsMenuToSave()
    })

    viewModel.saved.observe(viewLifecycleOwner, {
      if (it) {
        findNavController().navigateUp()
      }
    })
  }

  private fun changeOptionsMenuToSave() {
    menu?.apply {
      findItem(R.id.profile_picture_edit_item)?.isVisible = false
      findItem(R.id.profile_picture_save_item)?.isVisible = true
    }
  }

  override fun onClick(v: View) {
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.profile_picture_edit_item -> checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
      R.id.profile_picture_save_item -> viewModel.save()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun choosePicture() {
    val openGalleryIntent = Intent(
      Intent.ACTION_PICK,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    ).apply {
      type = "image/*"
    }
    intentLauncher.launch(openGalleryIntent)
  }

  override fun onPermissionsGranted() {
    choosePicture()
  }

  override fun onIntentResult(data: Intent?) {
    data?.data?.let { uri ->
      FileUtil.getFileAbsolutePath(mContext.contentResolver, uri)?.let {
        viewModel.setProfilePicture(mContext, it)
      }
    }
  }
}