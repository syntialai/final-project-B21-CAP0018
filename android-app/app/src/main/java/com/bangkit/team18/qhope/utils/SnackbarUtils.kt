package com.bangkit.team18.qhope.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.bangkit.team18.core.utils.view.ViewUtils.remove
import com.bangkit.team18.qhope.R
import com.google.android.material.snackbar.Snackbar

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