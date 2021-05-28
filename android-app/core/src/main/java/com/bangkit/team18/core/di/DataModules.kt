package com.bangkit.team18.core.di

import com.bangkit.team18.core.data.repository.AuthRepositoryImpl
import com.bangkit.team18.core.data.repository.HospitalRepositoryImpl
import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.impl.HospitalRemoteDataSourceImpl
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.core.domain.repository.HospitalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { HospitalRepositoryImpl(get(), get()) } bind HospitalRepository::class
    single { AuthRepositoryImpl(get()) } bind AuthRepository::class
}

@ExperimentalCoroutinesApi
val remoteDataSourceModule = module {
    single { HospitalRemoteDataSourceImpl(get()) } bind HospitalRemoteDataSource::class
}
