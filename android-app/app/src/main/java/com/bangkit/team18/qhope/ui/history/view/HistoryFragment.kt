package com.bangkit.team18.qhope.ui.history.view

import android.view.View
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.databinding.FragmentHistoryBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.history.adapter.HistoryAdapter
import com.bangkit.team18.qhope.ui.history.adapter.HistoryItemCallback
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.koin.android.ext.android.inject

class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryViewModel>(
  FragmentHistoryBinding::inflate,
  HistoryViewModel::class
), HistoryItemCallback {

  companion object {
    fun newInstance() = HistoryFragment()
  }

  private val storage: FirebaseStorage by inject()

  private val historyAdapter by lazy {
    HistoryAdapter(this)
  }

  override fun setupViews() {
    binding.recyclerViewBookingHistory.apply {
      adapter = historyAdapter
      setHasFixedSize(false)
    }
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.user.observe(viewLifecycleOwner, {
      it?.let { user ->
        viewModel.fetchUserBookingHistories(user.uid)
      } ?: run {
        viewModel.logOut()
      }
    })
    viewModel.bookingHistories.observe(viewLifecycleOwner, { histories ->
      showEmptyState(histories.isEmpty())
      historyAdapter.submitList(histories)
    })
  }

  override fun onClick(view: View?) {
    // No Implementation Needed
  }

  override fun showEmptyState(isEmpty: Boolean) {
    binding.apply {
      viewHistoryEmptyState.showOrRemove(isEmpty)
      recyclerViewBookingHistory.showOrRemove(isEmpty.not())
    }
  }

  override fun showLoadingState(isLoading: Boolean) {
    binding.spinKitLoadHistory.showOrRemove(isLoading)
  }

  override fun onClickListener(id: String) {
    findNavController().navigate(
      HistoryFragmentDirections.actionHistoryFragmentToHistoryDetailFragment(
        id
      )
    )
  }

  override fun getStorageRef(imagePath: String): StorageReference {
    return storage.getReference(imagePath)
  }
}