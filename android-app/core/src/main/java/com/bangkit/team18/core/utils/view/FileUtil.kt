package com.bangkit.team18.core.utils.view

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore

object FileUtil {
  fun getFileAbsolutePath(contentResolver: ContentResolver, uri: Uri): String? {
    var result: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(uri, projection, null, null, null)

    if (cursor?.moveToFirst() == true) {
      val columnIndex = cursor.getColumnIndex(projection[0])
      columnIndex.let {
        result = cursor.getString(it)
      }
    }
    cursor?.close()

    return result
  }
}