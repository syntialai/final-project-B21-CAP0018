package com.qhope.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.qhope.app.ui.login.view.LoginActivity
import com.qhope.app.ui.main.view.MainActivity
import com.qhope.app.ui.registration.view.IdVerificationActivity
import com.qhope.app.ui.registration.view.IdentityConfirmationActivity
import com.qhope.app.ui.registration.view.RegistrationActivity
import com.qhope.app.ui.registration.view.VerificationResultActivity

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