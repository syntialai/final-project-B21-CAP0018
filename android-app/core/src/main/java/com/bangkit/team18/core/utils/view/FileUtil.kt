package com.bangkit.team18.core.utils.view

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

  fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
      "JPEG_${timeStamp}_",
      ".jpg",
      storageDir
    )
  }

  fun getUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
      context,
      "com.bangkit.team18.qhope.fileprovider",
      file
    )
  }
}