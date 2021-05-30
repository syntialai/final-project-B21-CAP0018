package com.bangkit.team18.qhope.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bangkit.team18.core.utils.view.ViewUtils.remove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.LayoutEmptyStateViewBinding

class EmptyStateView constructor(context: Context, attrs: AttributeSet) :
  LinearLayout(context, attrs) {

  private lateinit var binding: LayoutEmptyStateViewBinding

  private val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView)

  init {
    initBinding()
    styledAttributes.apply {
      setImageRes(getDrawable(R.styleable.EmptyStateView_image))
      setTitle(getString(R.styleable.EmptyStateView_title))
      setDescription(getString(R.styleable.EmptyStateView_description))
      recycle()
    }
  }

  private fun setImageRes(drawable: Drawable?) {
    binding.imageViewEmptyState.setImageDrawable(drawable)
  }

  private fun setTitle(title: String?) {
    binding.textViewEmptyStateTitle.text = title
  }

  private fun setDescription(description: String?) {
    binding.textViewEmptyStateDescription.apply {
      description?.let {
        text = it
      } ?: run {
        remove()
      }
    }
  }

  private fun initBinding() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    binding = LayoutEmptyStateViewBinding.inflate(layoutInflater, this, true)
  }
}