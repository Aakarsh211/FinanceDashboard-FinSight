package com.example.finsight.model

data class StockInfo(
    val name: String?,
    val current_price: Double?,
    val previous_close: Double?,
    val open: Double?,
    val day_low: Double?,
    val day_high: Double?,
    val day_range: String?,
    val week_52_high: Double?,
    val week_52_low: Double?,
    val volume: Long?,
    val average_volume: Long?,
    val market_cap: String?,
    val pe_ratio: Double?,
    val dividend_yield: Double?,
    val sector: String?,
    val industry: String?
)
