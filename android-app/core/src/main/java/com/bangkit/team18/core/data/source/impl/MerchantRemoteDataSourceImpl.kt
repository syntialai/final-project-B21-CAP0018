package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.service.MerchantService
import com.bangkit.team18.core.data.source.MerchantRemoteDataSource
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token

class MerchantRemoteDataSourceImpl(private val merchantService: MerchantService) :
  MerchantRemoteDataSource {

  override suspend fun charge(var1: TokenRequestModel?): Token? =
    merchantService.charge(var1)
}