package com.becetti.tools.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class FrankfurterResponse(val amount: Double, val base: String, val date: String, val rates: Map<String, Double>)

interface FrankfurterApi {
    @GET("latest")
    suspend fun latest(@Query("from") base: String): FrankfurterResponse
}

data class CachedRates(val base: String, val date: String, val rates: Map<String, Double>, val fetchedAt: Long)
