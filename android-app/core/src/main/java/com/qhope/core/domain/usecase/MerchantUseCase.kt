package com.qhope.core.domain.usecase

import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface MerchantUseCase {

  suspend fun charge(var1: TokenRequestModel?): Flow<ResponseWrapper<Token?>>
}