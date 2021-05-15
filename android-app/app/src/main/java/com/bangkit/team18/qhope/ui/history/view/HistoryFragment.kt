package com.bangkit.team18.qhope.ui.history.view

import android.view.View
import com.bangkit.team18.qhope.databinding.FragmentHistoryBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.utils.view.ViewUtils.showOrRemove

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::inflate) {

  override fun setupViews() {
    binding.recyclerViewBookingHistory.apply {
//      adapter =
      setHasFixedSize(false)
    }
  }

  override fun onClick(view: View?) {
    // No Implementation Needed
  }

  private fun showEmptyState(isEmpty: Boolean) {
    with(binding) {
     viewHistoryEmptyState.showOrRemove(isEmpty)
     recyclerViewBookingHistory.showOrRemove(isEmpty.not())
    }
  }

  private fun showLoadingState(isLoading: Boolean) {
    binding.spinKitLoadHistory.showOrRemove(isLoading)
  }
}