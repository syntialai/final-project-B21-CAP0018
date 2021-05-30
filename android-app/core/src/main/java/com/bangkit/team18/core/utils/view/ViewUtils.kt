package com.bangkit.team18.core.utils.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bangkit.team18.core.utils.view.GlideApp.with
import com.bumptech.glide.Glide

object ViewUtils {

  fun <T> ImageView.loadImageFromStorage(
    context: Context, image: T,
    @DrawableRes placeholder: Int? = null
  ) {
    with(context).load(image).apply {
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
}