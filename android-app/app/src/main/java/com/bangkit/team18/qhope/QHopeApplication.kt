package com.bangkit.team18.qhope

import android.app.Application
import com.bangkit.team18.core.di.dispatcherModule
import com.bangkit.team18.core.di.firebaseModule
import com.bangkit.team18.core.di.remoteDataSourceModule
import com.bangkit.team18.core.di.repositoryModule
import com.bangkit.team18.core.di.useCaseModule
import com.bangkit.team18.qhope.di.viewModelModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@ExperimentalCoroutinesApi
class QHopeApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidLogger(Level.DEBUG)
      androidLogger(Level.ERROR)
      androidContext(this@QHopeApplication)
      modules(listOf(dispatcherModule, firebaseModule, remoteDataSourceModule, repositoryModule,
          useCaseModule, viewModelModule))
    }
  }
}