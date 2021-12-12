package com.bangkit.team18.core.data.repository

import com.bangkit.team18.core.data.repository.base.FetchDataWrapper
import com.bangkit.team18.core.data.source.MerchantRemoteDataSource
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.repository.MerchantRepository
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import kotlinx.coroutines.flow.Flow

class MerchantRepositoryImpl(private val merchantRemoteDataSource: MerchantRemoteDataSource) :
  MerchantRepository {

  override suspend fun charge(var1: TokenRequestModel?): Flow<ResponseWrapper<Token?>> {
    return object : FetchDataWrapper<Token?, Token?>() {
      override suspend fun fetchData(): Token? {
        return merchantRemoteDataSource.charge(var1)
      }

      override suspend fun mapData(response: Token?): Token? {
        return response
      }
    }.updateData()
  }
}