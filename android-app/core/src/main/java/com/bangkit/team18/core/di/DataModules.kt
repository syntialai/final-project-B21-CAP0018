package com.bangkit.team18.core.di

import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.impl.HospitalRemoteDataSourceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {

}

@ExperimentalCoroutinesApi
val remoteDataSourceModule = module {
  single { HospitalRemoteDataSourceImpl(get()) } bind HospitalRemoteDataSource::class
}
