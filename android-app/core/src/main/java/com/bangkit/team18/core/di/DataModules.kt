package com.bangkit.team18.core.di

import com.bangkit.team18.core.data.repository.AuthRepositoryImpl
import com.bangkit.team18.core.data.repository.HospitalRepositoryImpl
import com.bangkit.team18.core.data.repository.RoomBookingRepositoryImpl
import com.bangkit.team18.core.data.repository.UserRepositoryImpl
import com.bangkit.team18.core.data.source.AuthRemoteDataSource
import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.impl.AuthRemoteDataSourceImpl
import com.bangkit.team18.core.data.source.impl.HospitalRemoteDataSourceImpl
import com.bangkit.team18.core.data.source.impl.RoomBookingRemoteDataSourceImpl
import com.bangkit.team18.core.data.source.impl.UserRemoteDataSourceImpl
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.core.domain.repository.HospitalRepository
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import com.bangkit.team18.core.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.bind
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val repositoryModule = module {
  single { HospitalRepositoryImpl(get(), get()) } bind HospitalRepository::class
  single { AuthRepositoryImpl(get()) } bind AuthRepository::class
  single { UserRepositoryImpl(get(), get()) } bind UserRepository::class
  single { RoomBookingRepositoryImpl(get(), get()) } bind RoomBookingRepository::class
}

@ExperimentalCoroutinesApi
val remoteDataSourceModule = module {
  single { HospitalRemoteDataSourceImpl(get()) } bind HospitalRemoteDataSource::class
  single { RoomBookingRemoteDataSourceImpl(get(), get()) } bind RoomBookingRemoteDataSource::class
  single { AuthRemoteDataSourceImpl(get()) } bind AuthRemoteDataSource::class
  single { UserRemoteDataSourceImpl(get(), get()) } bind UserRemoteDataSource::class
}
