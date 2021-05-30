package com.bangkit.team18.qhope.ui.history.view

import android.view.View
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.databinding.FragmentHistoryBinding
import com.bangkit.team18.qhope.ui.base.adapter.OnItemClickListener
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.history.adapter.HistoryAdapter
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryViewModel

class HistoryFragment :
  BaseFragment<FragmentHistoryBinding, HistoryViewModel>(
    FragmentHistoryBinding::inflate,
    HistoryViewModel::class
  ), OnItemClickListener {

  companion object {
    fun newInstance() = HistoryFragment()
  }

  private val historyAdapter by lazy {
    HistoryAdapter(this)
  }

  override fun setupViews() {
    binding.recyclerViewBookingHistory.apply {
      adapter = historyAdapter
      setHasFixedSize(false)
    }
  }

  override fun onClick(view: View?) {
    // No Implementation Needed
  }

  override fun showEmptyState(isEmpty: Boolean) {
    with(binding) {
      viewHistoryEmptyState.showOrRemove(isEmpty)
      recyclerViewBookingHistory.showOrRemove(isEmpty.not())
    }
  }

  override fun showLoadingState(isLoading: Boolean) {
    binding.spinKitLoadHistory.showOrRemove(isLoading)
  }

  override fun onClickListener(id: String) {
    // TODO: Go to history detail
  }
}