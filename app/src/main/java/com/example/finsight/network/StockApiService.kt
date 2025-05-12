package com.example.finsight.network

import com.example.finsight.model.PricePoint
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    @GET("getPriceHistory")
    suspend fun getPriceHistory(
        @Query("ticker") ticker: String,
        @Query("range") range: String = "1M"
    ): List<PricePoint>
}