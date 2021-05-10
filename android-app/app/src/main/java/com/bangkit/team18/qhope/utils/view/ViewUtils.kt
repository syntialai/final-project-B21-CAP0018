package com.bangkit.team18.qhope.utils.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

object ViewUtils {

  fun <T> ImageView.loadImage(context: Context, image: T, @DrawableRes placeholder: Int? = null) {
    Glide.with(context).load(image).apply {
      placeholder?.let {
        this.placeholder(it).error(it)
      }
    }.into(this)
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
}