package com.example.finsight

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finsight.model.PricePoint
import com.example.finsight.model.StockInfo
import com.example.finsight.model.StockSearchResult
import com.example.finsight.network.RetrofitInstance
import com.example.finsight.ui.theme.StockDetailScreen
import com.example.finsight.ui.theme.StockSearchBar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.launch
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    StockInfoScreen(navController)
                }
                composable("stockNews/{ticker}") { backStackEntry ->
                    val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                    Text("News for $ticker")
                }
                composable("analystInsights/{ticker}") { backStackEntry ->
                    val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                    Text("Analyst Insights for $ticker")
                }
                composable("aiInsights/{ticker}") { backStackEntry ->
                    val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                    Text("AI Insights for $ticker")
                }
            }
        }
    }
}

@Composable
fun StockInfoScreen(navController: androidx.navigation.NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStock by remember { mutableStateOf<StockSearchResult?>(null) }
    var suggestions by remember { mutableStateOf<List<StockSearchResult>>(emptyList()) }
    var stockInfo by remember { mutableStateOf<StockInfo?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedRange by remember { mutableStateOf("1M") }
    var priceHistory by remember { mutableStateOf<List<PricePoint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Debounce search queries
    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            delay(300) // Debounce delay
            try {
                suggestions = RetrofitInstance.api.searchStocks(searchQuery)
            } catch (e: Exception) {
                error = "Failed to load suggestions"
                suggestions = emptyList()
            }
        } else {
            suggestions = emptyList()
        }
    }

    val onSearch = {
        if (searchQuery.isNotEmpty() && suggestions.isNotEmpty()) {
            // Auto-select first suggestion when pressing enter
            selectedStock = suggestions.first()
            searchQuery = "${suggestions.first().ticker} - ${suggestions.first().name}"
        }
    }

    // Load data when stock is selected
    LaunchedEffect(selectedStock) {
        selectedStock?.let { stock ->
            isLoading = true
            try {
                stockInfo = RetrofitInstance.api.getStockInfo(stock.ticker)
                priceHistory = RetrofitInstance.api.getPriceHistory(stock.ticker, selectedRange)
                error = null
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StockSearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            suggestions = suggestions,
            onSuggestionSelected = { result ->
                selectedStock = result
                searchQuery = "${result.ticker} - ${result.name}"
            },
            onSearch = onSearch
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        selectedStock?.let {
            // Display Stock Info
            stockInfo?.let { info ->
                Text("Name: ${info.name}", style = MaterialTheme.typography.titleMedium)
                Text("Current Price: ${info.current_price}")
                Text("Previous Close: ${info.previous_close}")
                Text("Open: ${info.open}")
                Text("Day Range: ${info.day_range}")
                Text("52-Week Range: ${info.week_52_low} – ${info.week_52_high}")
                Text("Volume: ${info.volume}")
                Text("Avg Volume: ${info.average_volume}")
                Text("Market Cap: ${info.market_cap}")
                Text("PE Ratio: ${info.pe_ratio ?: "N/A"}")
                Text("Dividend Yield: ${info.dividend_yield ?: "N/A"}")
                Text("Sector: ${info.sector ?: "N/A"}")
                Text("Industry: ${info.industry ?: "N/A"}")

                Spacer(modifier = Modifier.height(16.dp))

                // Graph Range Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("24h", "1W", "1M", "3M", "YTD", "1Y", "5Y").forEach { range ->
                        Button(
                            onClick = {
                                selectedRange = range
                                scope.launch {
                                    priceHistory = RetrofitInstance.api.getPriceHistory(it.ticker, range)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRange == range) Color.Gray else Color.LightGray
                            )
                        ) {
                            Text(text = range)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Graph
                if (priceHistory.isNotEmpty()) {
                    StockPriceChart(it.ticker, priceHistory)
                } else {
                    Text("Loading graph...", modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Buttons
                Button(
                    onClick = { navController.navigate("stockNews/${it.ticker}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Stock News")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("analystInsights/${it.ticker}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Analyst Insights")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("aiInsights/${it.ticker}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("AI Insights")
                }
            }
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun StockPriceChart(ticker: String, priceHistory: List<PricePoint>) {
    val entries = remember(priceHistory) {
        priceHistory.mapIndexed { index, point ->
            Entry(index.toFloat(), point.price.toFloat())
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false

                marker = object : MarkerView(context, android.R.layout.simple_list_item_1) {
                    private val textView: TextView = findViewById(android.R.id.text1)
                    override fun refreshContent(e: Entry?, highlight: Highlight?) {
                        val index = e?.x?.toInt() ?: 0
                        val price = e?.y ?: 0f
                        val timestamp = priceHistory.getOrNull(index)?.timestamp ?: "N/A"
                        textView.text = "$timestamp\n$${price}"
                        super.refreshContent(e, highlight)
                    }

                    override fun getOffset(): MPPointF {
                        return MPPointF(-(width / 2f), -height.toFloat())
                    }
                }
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Price").apply {
                color = Color.Blue.toArgb()
                setDrawCircles(false)
                setDrawValues(false)
                lineWidth = 2f
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}