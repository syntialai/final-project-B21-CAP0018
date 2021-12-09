package com.bangkit.team18.qhope

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
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
class QHopeApplication : MultiDexApplication() {

  override fun attachBaseContext(base: Context?) {
    MultiDex.install(this)
    super.attachBaseContext(base)
  }

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