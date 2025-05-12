package com.example.finsight

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finsight.model.PricePoint
import com.example.finsight.network.StockApiService
import com.example.finsight.ui.theme.CustomMarkerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun StockPriceChart(ticker: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var priceData by remember { mutableStateOf<List<PricePoint>>(emptyList()) }
    var selectedRange by remember { mutableStateOf("1M") }

    val api = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StockApiService::class.java)
    }

    LaunchedEffect(selectedRange) {
        try {
            priceData = api.getPriceHistory(ticker, selectedRange)
        } catch (e: Exception) {
            Log.e("GraphError", e.toString())
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = "${ticker.uppercase()} Price History",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("24h", "1W", "1M", "3M", "YTD", "1Y", "5Y").forEach { range ->
                Button(
                    onClick = { selectedRange = range },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRange == range) Color.Gray else Color.LightGray
                    )
                ) {
                    Text(text = range)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (priceData.isNotEmpty()) {
            val entries = priceData.mapIndexed { index, point ->
                Entry(index.toFloat(), point.price.toFloat())
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                factory = { ctx ->
                    LineChart(ctx).apply {
                        val marker = CustomMarkerView(ctx)
                        marker.chartView = this
                        this.marker = marker

                        description.isEnabled = false
                        setTouchEnabled(true)
                        setPinchZoom(true)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        axisRight.isEnabled = false
                        val dataSet = LineDataSet(entries, "Price").apply {
                            color = Color.Blue.toArgb()
                            valueTextColor = Color.Black.toArgb()
                            setDrawCircles(false)
                            setDrawValues(false)
                        }
                        data = LineData(dataSet)
                        invalidate()
                    }
                }
            )
        } else {
            Text(
                text = "Loading graph...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

