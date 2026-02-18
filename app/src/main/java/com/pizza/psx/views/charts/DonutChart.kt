package com.pizza.psx.views.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign

@Composable
fun DonutChartWithLegend(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    isShowCentralContent: Boolean = true,
    isShowTextInCenter: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier.size(size = 200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Chart
            InteractiveDonutChart(
                data = data,
                strokeWidth = 10.dp,
                radius = 100.dp,
                modifier = modifier.padding(16.dp),
            )
          if(isShowCentralContent) {
              Box(
                  modifier = modifier
                      .size((100 * 1.2f).dp)
                      .background(Color.Transparent),
                  contentAlignment = Alignment.Center
              ) {
                  CenterContent(data = data)
              }
          }

            if(isShowTextInCenter){
                Box(
                    modifier = modifier
                        .size((100 * 1.2f).dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    RotatingCenterLegend(data)
                }
            }
        }


        // Legend
        Legend(
            data = data,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun CenterContent(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    val totalValue = remember(data) {
        data.sumOf { it.price.toDouble() }
    }

    val totalHoldings = data.size

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Total Value
        Text(
            text = "Total Value",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        Text(
            text = "${totalValue.toFormattedCurrency()}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )



    }
}

// Helper extension for formatting
fun Double.toFormattedCurrency(): String {
    return when {
        this >= 1_000_000_000 -> String.format("%.2fB", this / 1_000_000_000)
        this >= 1_000_000 -> String.format("%.2fM", this / 1_000_000)
        this >= 1_000 -> String.format("%.2fK", this / 1_000)
        else -> String.format("%.2f", this)
    }
}

@Composable
fun Legend(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    columns: Int = 4
) {
    // Calculate how many rows we need
    val rows = (data.size + columns - 1) / columns

    Column(modifier = modifier) {
        repeat(rows) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (colIndex in 0 until columns) {
                    val itemIndex = rowIndex * columns + colIndex
                    if (itemIndex < data.size) {
                        val item = data[itemIndex]
                        val total = data.sumOf { it.value.toDouble() }
                        val percentage = (item.value / total * 100).toFloat()

                        Box(modifier = Modifier.weight(1f)) {
                            LegendItem(
                                chartData = item,
                                percentage = percentage,
                            )
                        }
                    } else {
                        // Empty space for incomplete row
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun LegendItem(chartData: ChartData, percentage: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(chartData.color, CircleShape)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column() {
            Text(
                text = chartData.label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${String.format("%.1f", percentage)}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Composable
fun RotatingCenterLegend(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    intervalMillis: Long = 6000L
) {
    if (data.isEmpty()) return

    val total = remember(data) {
        data.sumOf { it.value.toDouble() }
    }

    var currentIndex by remember { mutableStateOf(0) }

    // Auto change every 6 seconds
    LaunchedEffect(data) {
        while (true) {
            delay(intervalMillis)
            currentIndex = (currentIndex + 1) % data.size
        }
    }

    val item = data[currentIndex]
    val percentage = (item.value / total * 100).toFloat()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(item.color, CircleShape)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${String.format("%.1f", percentage)}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
