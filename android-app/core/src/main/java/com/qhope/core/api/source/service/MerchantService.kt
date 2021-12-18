package com.qhope.core.api.source.service

import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MerchantService {

  @Headers("Content-Type: application/json", "Accept: application/json")
  @POST("charge")
  suspend fun charge(@Body var1: TokenRequestModel?): Token?
}