package com.bangkit.team18.qhope.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.team18.qhope.ui.history.view.HistoryFragment
import com.bangkit.team18.qhope.ui.home.view.HomeFragment

class MainAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

  companion object {
    private const val MAIN_PAGER_COUNT = 3
    const val HOME_FRAGMENT_INDEX = 0
    const val HISTORY_FRAGMENT_INDEX = 1
    const val PROFILE_FRAGMENT_INDEX = 2
  }

  override fun getItemCount(): Int = MAIN_PAGER_COUNT

  // TODO: Add required fragment
  override fun createFragment(position: Int): Fragment = when(position) {
    HOME_FRAGMENT_INDEX -> HomeFragment.newInstance()
    HISTORY_FRAGMENT_INDEX -> HistoryFragment.newInstance()
    else -> Fragment()
  }
}