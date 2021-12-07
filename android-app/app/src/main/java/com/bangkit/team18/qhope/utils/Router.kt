package com.bangkit.team18.qhope.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bangkit.team18.qhope.ui.login.view.LoginActivity
import com.bangkit.team18.qhope.ui.main.view.MainActivity
import com.bangkit.team18.qhope.ui.registration.view.IdVerificationActivity
import com.bangkit.team18.qhope.ui.registration.view.IdentityConfirmationActivity
import com.bangkit.team18.qhope.ui.registration.view.RegistrationActivity
import com.bangkit.team18.qhope.ui.registration.view.VerificationResultActivity

object Router {

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

  fun goToVerificationResult(context: Context) {
    val intent = Intent(context, VerificationResultActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
  }

  fun goToIdVerification(context: Context, newTask: Boolean = true) {
    val intent = Intent(context, IdVerificationActivity::class.java).apply {
      if (newTask) {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
    }
    context.startActivity(intent)
  }

  fun goToIdentityConfirmation(context: Context, newTask: Boolean = true) {
    val intent = Intent(context, IdentityConfirmationActivity::class.java).apply {
      if (newTask) {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
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
      setDataAndType(Uri.parse(pdfUrl), HTML_TYPE)
    }
    context.startActivity(pdfIntent)
  }
}