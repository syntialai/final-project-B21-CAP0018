package com.bangkit.team18.core.data.source

import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token

interface MerchantRemoteDataSource {

  suspend fun charge(var1: TokenRequestModel?): Token?
}