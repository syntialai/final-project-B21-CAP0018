package com.bangkit.team18.qhope.utils

import android.content.Context
import android.view.View
import com.bangkit.team18.qhope.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtil {
  fun checkPermissions(
    context: Context,
    permissions: List<String>,
    onPermissionsGranted: () -> Unit,
    onAnyPermissionsDenied: (List<String>) -> Unit
  ) {
    Dexter.withContext(context)
      .withPermissions(permissions)
      .withListener(object : MultiplePermissionsListener {
        override fun onPermissionsChecked(permissionsReport: MultiplePermissionsReport) {
          if (permissionsReport.areAllPermissionsGranted()) {
            onPermissionsGranted.invoke()
          } else {
            val reCheckPermissions = permissionsReport
              .deniedPermissionResponses
              .filter { !it.isPermanentlyDenied }
              .map { it.permissionName }
            onAnyPermissionsDenied.invoke(reCheckPermissions)
          }
        }

        override fun onPermissionRationaleShouldBeShown(
          permissionsRequest: MutableList<PermissionRequest>?,
          token: PermissionToken?
        ) {
          token?.continuePermissionRequest()
        }
      }).check()
  }

  fun onAnyPermissionsDenied(view: View, permissions: List<String>) {
    if (permissions.isNotEmpty()) {
      SnackbarUtils.showErrorSnackbar(
        view,
        view.context.getString(R.string.denied_permission_message)
      )
    } else {
      SnackbarUtils.showErrorSnackbar(
        view,
        view.context.getString(R.string.permanently_denied_permission_message)
      )
    }
  }
}