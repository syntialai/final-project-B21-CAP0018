package com.bangkit.team18.qhope

import android.app.Application
import com.bangkit.team18.core.di.*
import com.bangkit.team18.qhope.di.viewModelModule
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber


@ExperimentalCoroutinesApi
class QHopeApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    
    FirebaseApp.initializeApp(this)
    
    startKoin {
      androidLogger(Level.DEBUG)
      androidLogger(Level.ERROR)
      androidContext(this@QHopeApplication)
      modules(
        listOf(
          dispatcherModule,
          firebaseModule,
          sharedPrefRepositoryModule,
          networkModule,
          serviceModule,
          remoteDataSourceModule,
          repositoryModule,
          useCaseModule,
          viewModelModule
        )
      )
    }

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}