package com.bangkit.team18.qhope.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.WidgetLayoutBannerInfoBinding
import com.bangkit.team18.qhope.ui.widget.callback.OnBannerActionButtonClickListener

class BannerInfo constructor(context: Context, attrs: AttributeSet) :
  ConstraintLayout(context, attrs) {

  private lateinit var binding: WidgetLayoutBannerInfoBinding

  private var actionButtonClickListener: OnBannerActionButtonClickListener? = null

  private val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.BannerInfo)

  init {
    initBinding()
    styledAttributes.apply {
      setDescription(getString(R.styleable.BannerInfo_description))
      setActionButtonLabel(getString(R.styleable.BannerInfo_button_label))
      setButtonVisibility(getBoolean(R.styleable.BannerInfo_button_visible, true))
      recycle()
    }
  }

  fun setActionButtonOnClickListener(listener: OnBannerActionButtonClickListener) {
    actionButtonClickListener = listener
    binding.buttonBannerInfoAction.setOnClickListener {
      actionButtonClickListener?.onBannerButtonClicked()
    }
  }

  private fun setDescription(description: String?) {
    binding.textViewBannerInfoDescription.text = description
  }

  private fun setActionButtonLabel(buttonLabel: String?) {
    binding.buttonBannerInfoAction.text = buttonLabel
  }

  private fun setButtonVisibility(show: Boolean) {
    binding.buttonBannerInfoAction.showOrRemove(show)
  }

  private fun initBinding() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    binding = WidgetLayoutBannerInfoBinding.inflate(layoutInflater, this, true)
  }
}