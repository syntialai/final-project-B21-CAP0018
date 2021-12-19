package com.qhope.core.domain.usecase.interactor

import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.repository.MerchantRepository
import com.qhope.core.domain.usecase.MerchantUseCase
import kotlinx.coroutines.flow.Flow

class MerchantInteractor(private val merchantRepository: MerchantRepository) : MerchantUseCase {

  override suspend fun charge(var1: TokenRequestModel?): Flow<ResponseWrapper<Token?>> =
    merchantRepository.charge(var1)
}