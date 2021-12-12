package com.bangkit.team18.core.domain.usecase.interactor

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.repository.MerchantRepository
import com.bangkit.team18.core.domain.usecase.MerchantUseCase
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import kotlinx.coroutines.flow.Flow

class MerchantInteractor(private val merchantRepository: MerchantRepository) : MerchantUseCase {

  override suspend fun charge(var1: TokenRequestModel?): Flow<ResponseWrapper<Token?>> =
    merchantRepository.charge(var1)
}