package com.qhope.app.ui.tnc.view

import android.os.Bundle
import android.view.View
import com.qhope.app.R
import com.qhope.app.databinding.ActivityTermsAndConditionBinding
import com.qhope.app.ui.base.view.BaseActivity
import com.qhope.app.ui.tnc.adapter.TermsAndConditionAdapter

class TermsAndConditionActivity :
  BaseActivity<ActivityTermsAndConditionBinding>(ActivityTermsAndConditionBinding::inflate) {

  private val termsAndConditionAdapter by lazy {
    TermsAndConditionAdapter()
  }

  override fun setupViews(savedInstanceState: Bundle?) {
    binding.apply {
      toolbarTnc.setNavigationOnClickListener(this@TermsAndConditionActivity)

      rvTnc.adapter = termsAndConditionAdapter
    }
    termsAndConditionAdapter.submitList(getData())
  }

  override fun onClick(v: View?) {
    when (v) {
      binding.toolbarTnc -> onBackPressed()
    }
  }

  private fun getData(): List<Pair<String, String>> {
    val titles = resources.getStringArray(R.array.terms_and_condition_titles)
    val descriptions = resources.getStringArray(R.array.terms_and_condition_descriptions)

    return titles.mapIndexed { index, title ->
      Pair(title, descriptions.getOrNull(index).orEmpty())
    }
  }
}