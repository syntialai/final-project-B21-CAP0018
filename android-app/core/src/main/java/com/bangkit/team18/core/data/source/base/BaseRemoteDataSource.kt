package com.bangkit.team18.core.data.source.base

import android.net.Uri
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.core.utils.view.DataUtils.orZero
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
abstract class BaseRemoteDataSource {

  companion object {
    private const val GEO_HASH = "geohash"
    private const val LAT = "lat"
    private const val LNG = "lng"
    private const val DEFAULT_RADIUS = 10000
  }

  protected fun <T : Any> CollectionReference.loadData(
      clazz: Class<T>): Flow<List<T>> = channelFlow {
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

  protected fun <T : Any> CollectionReference.loadNearby(clazz: Class<T>, location: GeoLocation,
      radiusInMeter: Int = DEFAULT_RADIUS): Flow<List<T>> = callbackFlow {
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

  protected suspend fun StorageReference.addFile(fileUri: Uri, contentType: String): Flow<Boolean> {
    val metadata = storageMetadata {
      this.contentType = contentType
    }
    return channelFlow {
      val storageTask = putFile(fileUri, metadata).addOnSuccessListener {
        launch {
          send(true)
        }
      }.addOnFailureListener { exception ->
        close(exception)
      }

      awaitClose {
        storageTask.cancel()
      }
    }
  }

  private fun getPercentage(task: UploadTask.TaskSnapshot): Int {
    return (task.bytesTransferred.toFloat() / task.totalByteCount.toFloat()).toInt() * 100
  }

  private fun getQueryBounds(reference: CollectionReference, location: GeoLocation,
      radiusInMeter: Int): ArrayList<Task<QuerySnapshot>> {
    val bounds = GeoFireUtils.getGeoHashQueryBounds(location, radiusInMeter.toDouble())
    val tasks = arrayListOf<Task<QuerySnapshot>>()
    bounds.forEach { bound ->
      tasks.add(reference.orderBy(GEO_HASH).startAt(bound.startHash).endAt(bound.endHash).get())
    }
    return tasks
  }

  private fun getFilteredData(tasks: ArrayList<Task<QuerySnapshot>>, location: GeoLocation,
      radiusInMeter: Int): ArrayList<DocumentSnapshot> {
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
}