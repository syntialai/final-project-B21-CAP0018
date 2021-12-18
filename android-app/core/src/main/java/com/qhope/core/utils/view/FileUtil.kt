package com.qhope.core.utils.view

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

  fun getTemporaryFile(context: Context, uri: Uri): File? {
    uri.let {
      try {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex: Int? = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val fileName = nameIndex?.let { it1 -> cursor.getString(it1) }
          ?: SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        cursor?.close()

        val inputStream = context.contentResolver.openInputStream(it)
        val file =
          File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return file
      } catch (ex: Exception) {
        ex.printStackTrace()
      }
    }

    return null
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
      "com.qhope.app",
      file
    )
  }
}