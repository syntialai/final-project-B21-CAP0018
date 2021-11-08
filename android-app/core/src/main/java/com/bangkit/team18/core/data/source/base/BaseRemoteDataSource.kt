package com.bangkit.team18.core.data.source.base

import android.net.Uri
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.core.utils.view.DataUtils.orZero
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("RemoveExplicitTypeArguments")
@ExperimentalCoroutinesApi
abstract class BaseRemoteDataSource {

  companion object {
    private const val GEO_HASH = "geohash"
    private const val LAT = "lat"
    private const val LNG = "lng"
    private const val DEFAULT_RADIUS = 10000
  }

  protected fun <T : Any> CollectionReference.loadData(
    clazz: Class<T>
  ): Flow<List<T>> = channelFlow {
    val subscription = addSnapshotListener { snapshot, error ->
      error?.let { err ->
        close(err)
        return@addSnapshotListener
      } ?: run {
        launch {
          if (snapshot.isNull() || snapshot!!.isEmpty) {
            send(emptyList<T>())
          } else {
            send(snapshot.map { document ->
              document.toObject(clazz)
            })
          }
        }
      }
    }

    awaitClose {
      subscription.remove()
    }
  }

  protected fun <T : Any> DocumentReference.loadData(clazz: Class<T>): Flow<T?> = channelFlow {
    val subscription = addSnapshotListener { snapshot, error ->
      error?.let { err ->
        close(err)
        return@addSnapshotListener
      } ?: run {
        launch {
          if (snapshot.isNull() || snapshot!!.exists().not()) {
            send(null)
          } else {
            send(snapshot.toObject(clazz))
          }
        }
      }
    }

    awaitClose {
      subscription.remove()
    }
  }

  private fun <T : Any> CollectionReference.loadNearby(
    clazz: Class<T>, location: GeoLocation,
    radiusInMeter: Int = DEFAULT_RADIUS
  ): Flow<List<T>> = callbackFlow {
    val nearbyTasks = getQueryBounds(this@loadNearby, location, radiusInMeter)
    Tasks.whenAllComplete(nearbyTasks).addOnCompleteListener {
      val validDocuments = getFilteredData(nearbyTasks, location, radiusInMeter)
      offer(validDocuments.mapNotNull { snapshot ->
        snapshot.toObject(clazz)
      })
    }.addOnFailureListener {
      close(it)
    }.addOnCanceledListener {
      close()
    }

    awaitClose {
      nearbyTasks.forEach { _ ->
        cancel()
      }
    }
  }

  protected fun <T : Any> Query.loadData(clazz: Class<T>): Flow<List<T>> = channelFlow {
    val subscription = addSnapshotListener { snapshot, error ->
      error?.let { err ->
        close(err)
        return@addSnapshotListener
      } ?: run {
        launch {
          if (snapshot.isNull() || snapshot!!.isEmpty) {
            send(emptyList<T>())
          } else {
            send(snapshot.map { document ->
              document.toObject(clazz)
            })
          }
        }
      }
    }

    awaitClose {
      subscription.remove()
    }
  }

  protected suspend fun CollectionReference.addData(data: HashMap<String, Any>) {
    return suspendCoroutine { continuation ->
      add(data).addOnSuccessListener {
        continuation.resume(Unit)
      }.addOnFailureListener { exception ->
        continuation.resumeWithException(exception)
      }
    }
  }

  protected suspend fun CollectionReference.updateData(
    documentId: String,
    data: HashMap<String, Any?>
  ) {
    return suspendCoroutine { continuation ->
      document(documentId).set(data, SetOptions.merge()).addOnSuccessListener {
        continuation.resume(Unit)
      }.addOnFailureListener { exception ->
        continuation.resumeWithException(exception)
      }
    }
  }

  protected suspend fun StorageReference.addFile(fileUri: Uri): Flow<Uri> {
    return callbackFlow {
      putFile(fileUri).continueWithTask { task ->
        if (task.isSuccessful.not()) {
          close(task.exception)
        }
        this@addFile.downloadUrl
      }.addOnCompleteListener {
        if (it.result.isNull()) close(it.exception)
        offer(it.result)
      }.addOnFailureListener {
        close(it)
      }

      awaitClose {}
    }
  }

  protected fun <T> Flow<T>.transformToResponse(): Flow<ResponseWrapper<T>> {
    return transform { value ->
      emit(ResponseWrapper.Success(value) as ResponseWrapper<T>)
    }.onStart {
      emit(ResponseWrapper.Loading())
    }.catch { exception ->
      if (exception is IOException) {
        emit(ResponseWrapper.NetworkError())
      } else {
        emit(ResponseWrapper.Error(exception.message))
      }
    }
  }

  protected suspend fun StorageReference.addAndGetFileUrl(
    fileUri: Uri,
    contentType: String
  ): Flow<ResponseWrapper<Uri>> {
    val metadata = storageMetadata {
      this.contentType = contentType
    }
    return channelFlow {
      send(ResponseWrapper.Loading())
      putFile(fileUri, metadata).continueWithTask { task ->
        task.exception?.let {
          close(it)
        }
        this@addAndGetFileUrl.downloadUrl
      }.addOnSuccessListener { uri ->
        launch {
          send(ResponseWrapper.Success(uri))
        }
      }.addOnFailureListener { exception ->
        close(exception)
      }

      awaitClose {
        // No implementation needed
      }
    }
  }

  private fun getPercentage(task: UploadTask.TaskSnapshot): Int {
    return (task.bytesTransferred.toFloat() / task.totalByteCount.toFloat()).toInt() * 100
  }

  private fun getQueryBounds(
    reference: CollectionReference, location: GeoLocation,
    radiusInMeter: Int
  ): ArrayList<Task<QuerySnapshot>> {
    val bounds = GeoFireUtils.getGeoHashQueryBounds(location, radiusInMeter.toDouble())
    val tasks = arrayListOf<Task<QuerySnapshot>>()
    bounds.forEach { bound ->
      tasks.add(reference.orderBy(GEO_HASH).startAt(bound.startHash).endAt(bound.endHash).get())
    }
    return tasks
  }

  private fun getFilteredData(
    tasks: ArrayList<Task<QuerySnapshot>>, location: GeoLocation,
    radiusInMeter: Int
  ): ArrayList<DocumentSnapshot> {
    val filteredDocuments = arrayListOf<DocumentSnapshot>()
    tasks.forEach { task ->
      filteredDocuments.addAll(task.result.documents.filter { document ->
        val lat = document.getDouble(LAT).orZero()
        val lng = document.getDouble(LNG).orZero()

        GeoFireUtils.getDistanceBetween(GeoLocation(lat, lng), location) <= radiusInMeter
      })
    }
    return filteredDocuments
  }

  protected fun Task<AuthResult>.loadData(): Flow<ResponseWrapper<FirebaseUser>> = callbackFlow {
    offer(ResponseWrapper.Loading())
    addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        close(task.exception)
      } else {
        offer(ResponseWrapper.Success(task.result.user as FirebaseUser))
      }
    }
    awaitClose { }
  }

  protected fun <T> T.loadAsFlow(): Flow<T> {
    return flow {
      emit(this@loadAsFlow)
    }
  }
}