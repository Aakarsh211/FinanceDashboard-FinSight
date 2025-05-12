package com.example.finsight.network

import com.example.finsight.model.StockInfo
import com.example.finsight.model.PricePoint
import com.example.finsight.model.StockSearchResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FinSightApi {

    @GET("stock/{ticker}")
    suspend fun getStockInfo(@Path("ticker") ticker: String): StockInfo

    @GET("getPriceHistory")
    suspend fun getPriceHistory(
        @Query("ticker") ticker: String,
        @Query("range") range: String = "1M"
    ): List<PricePoint>

    @GET("searchStocks")
    suspend fun searchStocks(@Query("query") query: String): List<StockSearchResult>
}
