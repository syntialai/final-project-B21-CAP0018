package com.qhope.app.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.qhope.app.R
import com.qhope.core.utils.view.ViewUtils.remove

object SnackbarUtils {

  fun showErrorSnackbar(view: View, message: String, closeable: Boolean = false) {
    getSnackbar(view, message, closeable).setBackgroundTint(
      ContextCompat.getColor(view.context, R.color.design_default_color_error)
    ).setTextColor(
      ContextCompat.getColor(view.context, R.color.white)
    ).show()
  }

  fun showSnackbar(view: View, message: String, closeable: Boolean = false) {
    getSnackbar(view, message, closeable).show()
  }

  private fun getSnackbar(view: View, message: String, closeable: Boolean) = Snackbar.make(
    view,
    message, Snackbar.LENGTH_SHORT
  ).apply {
    if (closeable) {
      setAction(R.string.close_action) { view ->
        view.remove()
      }
    }
  }
}