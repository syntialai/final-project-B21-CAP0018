package com.bangkit.team18.qhope.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bangkit.team18.qhope.ui.history.view.HistoryDetailFragment
import com.bangkit.team18.qhope.ui.login.view.LoginActivity
import com.bangkit.team18.qhope.ui.main.adapter.MainAdapter
import com.bangkit.team18.qhope.ui.main.view.MainActivity
import com.bangkit.team18.qhope.ui.registration.view.RegistrationActivity

object Router {

  const val PARAM_HISTORY_ID = "PARAM_HISTORY_ID"
  const val PARAM_MAIN_FIRST_FRAGMENT = "PARAM_MAIN_FIRST_FRAGMENT"

  private const val GOOGLE_DRIVE_VIEWER = "http://drive.google.com/viewer?url="
  private const val HTML_TYPE = "text/html"

  fun goToHistoryDetail(context: Context, id: String) {
    val intent = Intent(context, HistoryDetailFragment::class.java).apply {
      putExtra(PARAM_HISTORY_ID, id)
    }
    context.startActivity(intent)
  }

  fun goToLogin(context: Context) {
    val intent = Intent(context, LoginActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }

  fun goToMain(context: Context, firstFragment: Int = MainAdapter.HOME_FRAGMENT_INDEX) {
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra(PARAM_MAIN_FIRST_FRAGMENT, firstFragment)
      addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }

  fun goToRegistration(context: Context) {
    val intent = Intent(context, RegistrationActivity::class.java)
    context.startActivity(intent)
  }

  fun goToEditProfile(context: Context) {
    val intent = Intent()
    context.startActivity(intent)
  }

  fun openPdfFile(context: Context, pdfUrl: String) {
    val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(Uri.parse(GOOGLE_DRIVE_VIEWER + pdfUrl), HTML_TYPE)
    }
    context.startActivity(pdfIntent)
  }
}