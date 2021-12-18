package com.qhope.core.utils.view

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide


object ViewUtils {

  fun <T> ImageView.loadImageFromStorage(
    context: Context, image: T,
    @DrawableRes placeholder: Int? = null
  ) {
    Glide.with(context).load(image).apply {
      placeholder?.let {
        this.placeholder(it).error(it)
      }
    }.into(this)
  }

  fun <T> ImageView.loadImage(context: Context, image: T, @DrawableRes placeholder: Int? = null) {
    Glide.with(context).load(image).apply {
      placeholder?.let {
        this.placeholder(it).error(it)
      }
    }.into(this)
  }

  fun View.hide() {
    this.visibility = View.INVISIBLE
  }

  fun View.remove() {
    this.visibility = View.GONE
  }

  fun View.show() {
    this.visibility = View.VISIBLE
  }

  fun View.showOrRemove(isShow: Boolean) {
    this.visibility = if (isShow) {
      View.VISIBLE
    } else {
      View.GONE
    }
  }

  @ColorInt
  fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
  ): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
  }
}