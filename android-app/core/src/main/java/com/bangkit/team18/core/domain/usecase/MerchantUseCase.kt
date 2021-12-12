package com.bangkit.team18.core.domain.usecase

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import kotlinx.coroutines.flow.Flow

interface MerchantUseCase {

  suspend fun charge(var1: TokenRequestModel?): Flow<ResponseWrapper<Token?>>
}