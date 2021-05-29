package com.bangkit.team18.core.di

import com.bangkit.team18.core.data.repository.HospitalRepositoryImpl
import com.bangkit.team18.core.data.repository.RoomBookingRepositoryImpl
import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.impl.HospitalRemoteDataSourceImpl
import com.bangkit.team18.core.data.source.impl.RoomBookingRemoteDataSourceImpl
import com.bangkit.team18.core.domain.repository.HospitalRepository
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
  single { HospitalRepositoryImpl(get(), get()) } bind HospitalRepository::class
  single { RoomBookingRepositoryImpl(get(), get()) } bind RoomBookingRepository::class
}

@ExperimentalCoroutinesApi
val remoteDataSourceModule = module {
  single { HospitalRemoteDataSourceImpl(get()) } bind HospitalRemoteDataSource::class
  single { RoomBookingRemoteDataSourceImpl(get(), get()) } bind RoomBookingRemoteDataSource::class
}
