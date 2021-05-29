package com.bangkit.team18.core.utils.view

import android.app.Dialog
import android.content.Context
import android.view.Window
import androidx.annotation.LayoutRes
import com.bangkit.team18.core.utils.view.DataUtils.orFalse

object DialogUtils {

  fun createDialog(context: Context, @LayoutRes layoutRes: Int): Dialog {
    return Dialog(context).apply {
      requestWindowFeature(Window.FEATURE_NO_TITLE)
      setContentView(layoutRes)
      setCancelable(false)
      setCanceledOnTouchOutside(false)
    }
  }

  fun showDialog(dialog: Dialog?) {
    if (dialog?.isShowing.orFalse().not()) {
      dialog?.show()
    }
  }

  fun dismissDialog(dialog: Dialog?) {
    if (dialog?.isShowing.orFalse()) {
      dialog?.dismiss()
    }
  }
}