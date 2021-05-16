package com.bangkit.team18.qhope.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.withStyledAttributes
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.LayoutReferralLetterCardBinding
import com.google.android.material.circularreveal.cardview.CircularRevealCardView

class ReferralLetterCard constructor(context: Context, attrs: AttributeSet) :
    CircularRevealCardView(context, attrs) {

  companion object {
    const val PDF = 0
    const val IMAGE = 1
  }

  private lateinit var binding: LayoutReferralLetterCardBinding

  private var fileName: String? = null

  init {
    initBinding()
    context.withStyledAttributes(attrs, R.styleable.ReferralLetterCard) {
      setImage(getInt(R.styleable.ReferralLetterCard_image_type, PDF))
      setFileName(getString(R.styleable.ReferralLetterCard_file_name))
      recycle()
    }
  }

  fun setFileName(value: String?) {
    fileName = value
    binding.textViewReferralLetterFile.text = fileName
  }

  fun setImage(type: Int) {
    binding.imageViewReferralLetterFile.setImageResource(when (type) {
      PDF -> R.drawable.drawable_pdf_type
      else -> R.drawable.drawable_image_type
    })
  }

  private fun initBinding() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    binding = LayoutReferralLetterCardBinding.inflate(layoutInflater, this, true)
  }
}