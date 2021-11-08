package com.bangkit.team18.core.di

import com.bangkit.team18.core.BuildConfig
import com.bangkit.team18.core.config.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val firebaseModule = module {
  single { FirebaseFirestore.getInstance() }
  single { FirebaseAuth.getInstance() }
  single { FirebaseStorage.getInstance() }
}

val dispatcherModule = module {
  single { Dispatchers.IO }
}

val networkModule = module {
  fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
      setLevel(HttpLoggingInterceptor.Level.BODY)
    }
  }

  fun provideAuthInterceptor(): Interceptor {
    return Interceptor { chain ->
      val requestWithHeader = chain.request().newBuilder()
        .addHeader(Constants.X_ACCESS_TOKEN_HEADER, "")
        .build()
      chain.proceed(requestWithHeader)
    }
  }

  fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
    authInterceptor: Interceptor
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .addInterceptor(authInterceptor)
      .connectTimeout(Constants.CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
      .readTimeout(Constants.CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
      .build()
  }

  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.QHOPE_API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()
  }

  single {
    provideLoggingInterceptor()
    provideAuthInterceptor()
    provideOkHttpClient(get(), get())
    provideRetrofit(get())
  }
}
