package com.bangkit.team18.core.domain.repository

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import kotlinx.coroutines.flow.Flow

interface MerchantRepository {

  suspend fun charge(var1: TokenRequestModel?): Flow<ResponseWrapper<Token?>>
}