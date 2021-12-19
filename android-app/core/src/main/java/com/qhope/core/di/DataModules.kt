package com.qhope.core.di

import com.qhope.core.data.source.service.AuthService
import com.qhope.core.data.source.service.HospitalService
import com.qhope.core.data.source.service.MerchantService
import com.qhope.core.data.source.service.TransactionService
import com.qhope.core.data.source.service.UserService
import com.qhope.core.data.repository.AuthRepositoryImpl
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.data.repository.HospitalRepositoryImpl
import com.qhope.core.data.repository.MerchantRepositoryImpl
import com.qhope.core.data.repository.RoomBookingRepositoryImpl
import com.qhope.core.data.repository.UserRepositoryImpl
import com.qhope.core.data.source.AuthRemoteDataSource
import com.qhope.core.data.source.HospitalRemoteDataSource
import com.qhope.core.data.source.MerchantRemoteDataSource
import com.qhope.core.data.source.RoomBookingRemoteDataSource
import com.qhope.core.data.source.UserRemoteDataSource
import com.qhope.core.data.source.impl.AuthRemoteDataSourceImpl
import com.qhope.core.data.source.impl.HospitalRemoteDataSourceImpl
import com.qhope.core.data.source.impl.MerchantRemoteDataSourceImpl
import com.qhope.core.data.source.impl.RoomBookingRemoteDataSourceImpl
import com.qhope.core.data.source.impl.UserRemoteDataSourceImpl
import com.qhope.core.domain.repository.AuthRepository
import com.qhope.core.domain.repository.HospitalRepository
import com.qhope.core.domain.repository.MerchantRepository
import com.qhope.core.domain.repository.RoomBookingRepository
import com.qhope.core.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
val repositoryModule = module {
  single { HospitalRepositoryImpl(get(), get()) } bind HospitalRepository::class
  single { AuthRepositoryImpl(get()) } bind AuthRepository::class
  single { UserRepositoryImpl(get(), get()) } bind UserRepository::class
  single { RoomBookingRepositoryImpl(get(), get()) } bind RoomBookingRepository::class
  single { MerchantRepositoryImpl(get()) } bind MerchantRepository::class
}

val sharedPrefRepositoryModule = module {
  single {
    AuthSharedPrefRepository.newInstance(androidContext())
  }
}

@ExperimentalCoroutinesApi
val remoteDataSourceModule = module {
  single { HospitalRemoteDataSourceImpl(get()) } bind HospitalRemoteDataSource::class
  single { RoomBookingRemoteDataSourceImpl(get()) } bind RoomBookingRemoteDataSource::class
  single { AuthRemoteDataSourceImpl(get(), get()) } bind AuthRemoteDataSource::class
  single { UserRemoteDataSourceImpl(get()) } bind UserRemoteDataSource::class
  single { MerchantRemoteDataSourceImpl(get()) } bind MerchantRemoteDataSource::class
}

val serviceModule = module {
  fun provideAuthService(retrofit: Retrofit): AuthService {
    return retrofit.create(AuthService::class.java)
  }

  fun provideHospitalService(retrofit: Retrofit): HospitalService {
    return retrofit.create(HospitalService::class.java)
  }

  fun provideTransactionService(retrofit: Retrofit): TransactionService {
    return retrofit.create(TransactionService::class.java)
  }

  fun provideUserService(retrofit: Retrofit): UserService {
    return retrofit.create(UserService::class.java)
  }

  fun provideMerchantService(retrofit: Retrofit): MerchantService {
    return retrofit.create(MerchantService::class.java)
  }

  single { provideAuthService(get()) }
  single { provideHospitalService(get()) }
  single { provideTransactionService(get()) }
  single { provideUserService(get()) }
  single { provideMerchantService(get()) }
}
