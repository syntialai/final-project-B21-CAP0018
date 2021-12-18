package com.qhope.core.data.source.impl

import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import com.qhope.core.api.source.service.MerchantService
import com.qhope.core.data.source.MerchantRemoteDataSource

class MerchantRemoteDataSourceImpl(private val merchantService: MerchantService) :
  MerchantRemoteDataSource {

  override suspend fun charge(var1: TokenRequestModel?): Token? =
    merchantService.charge(var1)
}