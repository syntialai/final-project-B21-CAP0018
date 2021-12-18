package com.qhope.app.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.qhope.app.R
import com.qhope.app.databinding.WidgetLayoutReferralLetterCardBinding

class ReferralLetterCard constructor(context: Context, attrs: AttributeSet) :
  CircularRevealCardView(context, attrs) {

  companion object {
    const val PDF = 0
    const val IMAGE = 1
  }

  private lateinit var binding: WidgetLayoutReferralLetterCardBinding

  private val styledAttributes = context.obtainStyledAttributes(
    attrs,
    R.styleable.ReferralLetterCard
  )

  private var fileName: String? = null

  init {
    initBinding()

    setImage(styledAttributes.getInt(R.styleable.ReferralLetterCard_image_type, PDF))
    styledAttributes.getString(R.styleable.ReferralLetterCard_file_name)?.let { name ->
      setFileName(name)
    }
    styledAttributes.recycle()
  }

  fun setFileName(value: String?) {
    fileName = value
    binding.textViewReferralLetterFile.text = fileName
  }

  private fun setImage(type: Int) {
    binding.imageViewReferralLetterFile.setImageResource(
      when (type) {
        PDF -> R.drawable.drawable_pdf_type
        else -> R.drawable.drawable_image_type
      }
    )
  }

  fun setOnClick(block: () -> Unit) {
    binding.root.setOnClickListener {
      block.invoke()
    }
  }

  private fun initBinding() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    binding = WidgetLayoutReferralLetterCardBinding.inflate(layoutInflater, this, true)
  }
}