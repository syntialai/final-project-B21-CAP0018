package com.qhope.app.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

class ClickSpan(
  private val withUnderline: Boolean,
  private val onClickListener: View.OnClickListener
) : ClickableSpan() {

  override fun onClick(widget: View) {
    onClickListener.onClick(widget)
  }

  override fun updateDrawState(paint: TextPaint) {
    super.updateDrawState(paint)
    paint.isUnderlineText = withUnderline
  }

  companion object {

    fun setClickable(
      view: TextView,
      clickableText: String,
      withUnderline: Boolean = false,
      listener: View.OnClickListener
    ) {
      val text = view.text
      val span = ClickSpan(withUnderline, listener)
      val start = text.toString().indexOf(clickableText)
      val end = start + clickableText.length
      if (start == -1) {
        return
      }

      if (text is Spannable) {
        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      } else {
        val spannableString = SpannableString.valueOf(text)
        spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.text = spannableString
      }

      view.movementMethod = LinkMovementMethod.getInstance()
    }
  }
}