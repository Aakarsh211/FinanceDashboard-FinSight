package com.example.finsight.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.finsight.model.PricePoint
import com.example.finsight.model.StockInfo
import com.example.finsight.network.RetrofitInstance
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StockDetailScreen(ticker: String, navController: NavController? = null) {
    var stockInfo by remember { mutableStateOf<StockInfo?>(null) }
    var selectedRange by remember { mutableStateOf("1M") }
    var priceHistory by remember { mutableStateOf<List<PricePoint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Fetch data when range or ticker changes
    LaunchedEffect(ticker, selectedRange) {
        isLoading = true
        try {
            val stockDeferred = async { RetrofitInstance.api.getStockInfo(ticker) }
            val historyDeferred =
                async { RetrofitInstance.api.getPriceHistory(ticker, selectedRange) }
            stockInfo = stockDeferred.await()
            priceHistory = historyDeferred.await()
            error = null
        } catch (e: Exception) {
            error = "Error: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("${ticker.uppercase()} Price Chart", style = MaterialTheme.typography.headlineSmall)

        // Range Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("24h", "1W", "1M", "3M", "YTD", "1Y", "5Y").forEach { range ->
                Button(
                    onClick = { selectedRange = range },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRange == range) Color.Gray else Color.LightGray
                    ),
                    enabled = !isLoading
                ) {
                    Text(range)
                }
            }
        }

        // Graph Display
        if (priceHistory.isNotEmpty()) {
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
                        xAxis.setDrawGridLines(false)

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
                    val entries = priceHistory.mapIndexed { i, point ->
                        Entry(i.toFloat(), point.price.toFloat())
                    }
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
        } else {
            Text("Loading chart...", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stock Info Below Graph
        stockInfo?.let {
            Text("Name: ${it.name}")
            Text("Current Price: ${it.current_price}")
            Text("Open: ${it.open}")
            Text("Day Range: ${it.day_range}")
            Text("52W Range: ${it.week_52_low} – ${it.week_52_high}")
            Text("Volume: ${it.volume}")
            Text("Avg Volume: ${it.average_volume}")
            Text("Market Cap: ${it.market_cap}")
            Text("PE Ratio: ${it.pe_ratio ?: "N/A"}")
            Text("Dividend Yield: ${it.dividend_yield ?: "N/A"}")
            Text("Sector: ${it.sector ?: "N/A"}")
            Text("Industry: ${it.industry ?: "N/A"}")
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

    }
}

