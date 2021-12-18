package com.qhope.app.ui.tnc.view

import android.view.View
import com.qhope.app.R
import com.qhope.app.databinding.FragmentTermsAndConditionBinding
import com.qhope.app.ui.base.view.BaseFragment
import com.qhope.app.ui.base.viewmodel.BaseViewModel
import com.qhope.app.ui.tnc.adapter.TermsAndConditionAdapter

class TermsAndConditionFragment :
  BaseFragment<FragmentTermsAndConditionBinding, BaseViewModel>(
    FragmentTermsAndConditionBinding::inflate,
    BaseViewModel::class
  ) {

  private val termsAndConditionAdapter by lazy {
    TermsAndConditionAdapter()
  }

  override fun setupViews() {
    binding.rvTnc.adapter = termsAndConditionAdapter
    termsAndConditionAdapter.submitList(getData())
  }

  override fun onClick(v: View?) {
    // No implementation needed
  }

  private fun getData(): List<Pair<String, String>> {
    val titles = resources.getStringArray(R.array.terms_and_condition_titles)
    val descriptions = resources.getStringArray(R.array.terms_and_condition_descriptions)

    return titles.mapIndexed { index, title ->
      Pair(title, descriptions.getOrNull(index).orEmpty())
    }
  }
}