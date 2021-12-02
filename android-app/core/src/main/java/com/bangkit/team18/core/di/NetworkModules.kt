package com.bangkit.team18.core.di

import android.content.Context
import com.bangkit.team18.core.BuildConfig
import com.bangkit.team18.core.config.Constants
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.utils.NetworkUtils.getSslContext
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val firebaseModule = module {
  single { FirebaseAuth.getInstance() }
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

  fun provideAuthInterceptor(authSharedPrefRepository: AuthSharedPrefRepository): Interceptor {
    return Interceptor { chain ->
      val requestWithHeader = chain.request().newBuilder()
        .addHeader(Constants.X_ACCESS_TOKEN_HEADER, authSharedPrefRepository.idToken)
        .build()
      chain.proceed(requestWithHeader)
    }
  }

  fun provideOkHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor,
    authInterceptor: Interceptor
  ): OkHttpClient {
    val sslContextWithTMF = getSslContext(context)
    val okHttpClientBuilder = OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .addInterceptor(authInterceptor)
//      .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
      .connectTimeout(Constants.CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)
      .readTimeout(Constants.CONNECTION_TIMEOUT_SECOND, TimeUnit.SECONDS)

    sslContextWithTMF.second?.let {
      okHttpClientBuilder.sslSocketFactory(sslContextWithTMF.first.socketFactory, it)
    }

    return okHttpClientBuilder.build()
  }

  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.QHOPE_API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()
  }

  single { provideLoggingInterceptor() }
  single { provideAuthInterceptor(get()) }
  single { provideOkHttpClient(get(), get(), get()) }
  single { provideRetrofit(get()) }
}
