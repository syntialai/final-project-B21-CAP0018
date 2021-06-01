package com.bangkit.team18.qhope.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bangkit.team18.qhope.ui.login.view.LoginActivity
import com.bangkit.team18.qhope.ui.main.view.MainActivity
import com.bangkit.team18.qhope.ui.registration.view.IdVerificationActivity
import com.bangkit.team18.qhope.ui.registration.view.RegistrationActivity

object Router {

  private const val GOOGLE_DRIVE_VIEWER = "http://drive.google.com/viewer?url="
  private const val HTML_TYPE = "text/html"

  fun goToLogin(context: Context) {
    val intent = Intent(context, LoginActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
  }

  fun goToMain(context: Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
  }

  fun goToIdVerification(context: Context) {
    val intent = Intent(context, IdVerificationActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
  }

  fun goToRegistration(context: Context) {
    val intent = Intent(context, RegistrationActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
  }

  fun openPdfFile(context: Context, pdfUrl: String) {
    val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(Uri.parse(GOOGLE_DRIVE_VIEWER + pdfUrl), HTML_TYPE)
    }
    context.startActivity(pdfIntent)
  }
}