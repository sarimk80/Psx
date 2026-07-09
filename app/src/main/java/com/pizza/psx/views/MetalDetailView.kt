package com.pizza.psx.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.psx.R
import com.pizza.psx.domain.model.MetalsModel
import com.pizza.psx.presentation.viewModel.MetalViewModel
import java.util.Locale

// ---------------------------------------------------------------------------------------------
// Parsing / derived helpers — the API gives "yyyy-MM-dd HH:mm:ss" + a string price
// ---------------------------------------------------------------------------------------------

data class PricePoint(val dateKey: String, val price: Double)

private val MONTH_ABBR = arrayOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

fun MetalsModel.toPricePointOrNull(): PricePoint? {
    val price = max_price.toDoubleOrNull() ?: return null
    val dateKey = day.take(10) // "yyyy-MM-dd"
    return PricePoint(dateKey = dateKey, price = price)
}

fun String.toShortLabel(): String {
    // expects "yyyy-MM-dd"
    val parts = split("-")
    if (parts.size != 3) return this
    val month = parts[1].toIntOrNull()?.minus(1) ?: return this
    val day = parts[2].toIntOrNull() ?: return this
    val monthName = MONTH_ABBR.getOrNull(month) ?: return this
    return "$monthName $day"
}

fun Double.asCurrency(): String = "Rs. ${String.format(Locale.US, "%,.2f", this)}"

enum class RangeOption(val label: String, val days: Int) {
    WEEK("1W", 7),
    MONTH("1M", 30),
    THREE_MONTHS("3M", 90),
    SIX_MONTHS("6M", 180),
    YEAR("1Y", 365),
    ALL("All", Int.MAX_VALUE)
}

// ---------------------------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalDetailView(
    metal: String,
    onBackClick: () -> Unit
) {
    val viewModel: MetalViewModel = hiltViewModel()
    val uiState = viewModel.uiState.value

    LaunchedEffect(Unit) { viewModel.getMetal(metal) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = metal.uppercase(), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.metals == null -> MetalLoadingState()
                uiState.error != null -> MetalErrorState(
                    error = uiState.error,
                    onRetry = { viewModel.getMetal(metal) }
                )
                uiState.metals != null -> MetalDetailContent(metalName = metal, metals = uiState.metals)
                else -> MetalLoadingState()
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Content
// ---------------------------------------------------------------------------------------------

@Composable
fun MetalDetailContent(metalName: String, metals: List<MetalsModel>) {
    // Ascending by date (oldest -> newest) so the chart reads left-to-right correctly,
    // regardless of the order the API happens to return.
    val ascending = remember(metals) {
        metals.mapNotNull { it.toPricePointOrNull() }.sortedBy { it.dateKey }
    }

    if (ascending.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No price data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var selectedRange by remember { mutableStateOf(RangeOption.MONTH) }

    val filtered = remember(ascending, selectedRange) {
        if (selectedRange == RangeOption.ALL) ascending else ascending.takeLast(selectedRange.days)
    }

    val latest = ascending.last()
    val previous = if (ascending.size >= 2) ascending[ascending.size - 2] else null
    val dayChange = previous?.let { latest.price - it.price } ?: 0.0
    val dayChangePercent = previous?.let { if (it.price != 0.0) (dayChange / it.price) * 100 else 0.0 } ?: 0.0

    val periodHigh = filtered.maxOf { it.price }
    val periodLow = filtered.minOf { it.price }
    val periodAvg = filtered.map { it.price }.average()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MetalPriceHeader(
                metalName = metalName,
                currentPrice = latest.price,
                change = dayChange,
                changePercent = dayChangePercent,
                asOfLabel = latest.dateKey.toShortLabel()
            )
        }

        item {
            RangeSelectorRow(selectedRange = selectedRange, onRangeSelected = { selectedRange = it })
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AnimatedContent(
                        targetState = filtered,
                        label = "priceChart",
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) }
                    ) { points ->
                        PriceLineChart(
                            data = points.map { it.price },
                            lineColor = if (dayChange >= 0) financialGreen else financialRed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = filtered.first().dateKey.toShortLabel(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = filtered.last().dateKey.toShortLabel(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "Period High", value = periodHigh.asCurrency(), modifier = Modifier.weight(1f))
                StatCard(title = "Period Low", value = periodLow.asCurrency(), modifier = Modifier.weight(1f))
                StatCard(title = "Average", value = periodAvg.asCurrency(), modifier = Modifier.weight(1f))
            }
        }

        item {
            Text(
                text = "Recent Prices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        //val recentDescending = remember(filtered) { filtered.takeLast(10).reversed() }
        items(filtered.takeLast(10).reversed()) { point ->
            val index = ascending.indexOf(point)
            val prior = if (index > 0) ascending[index - 1] else null
            val delta = prior?.let { point.price - it.price } ?: 0.0
            RecentPriceRow(point = point, delta = delta)
        }

        item{
            Spacer(modifier = Modifier.padding(vertical = 30.dp))
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Header
// ---------------------------------------------------------------------------------------------

@Composable
fun MetalPriceHeader(
    metalName: String,
    currentPrice: Double,
    change: Double,
    changePercent: Double,
    asOfLabel: String
) {
    val isPositive = change >= 0
    val color = if (isPositive) financialGreen else financialRed

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "$metalName · as of $asOfLabel",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = currentPrice.asCurrency(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", change)} " +
                            "(${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", changePercent)}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "1D",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Range selector
// ---------------------------------------------------------------------------------------------

@Composable
fun RangeSelectorRow(selectedRange: RangeOption, onRangeSelected: (RangeOption) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RangeOption.entries.forEach { option ->
            FilterChip(
                selected = selectedRange == option,
                onClick = { onRangeSelected(option) },
                label = { Text(option.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Custom line chart (no external charting dependency)
// ---------------------------------------------------------------------------------------------

@Composable
fun PriceLineChart(
    data: List<Double>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val maxValue = data.max()
        val minValue = data.min()
        val range = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0
        val stepX = size.width / (data.size - 1)

        val linePath = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minValue) / range * size.height).toFloat()
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.22f), Color.Transparent)
            )
        )
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val lastX = (data.size - 1) * stepX
        val lastY = size.height - ((data.last() - minValue) / range * size.height).toFloat()
        drawCircle(color = lineColor, radius = 5.dp.toPx(), center = Offset(lastX, lastY))
        drawCircle(color = Color.White, radius = 2.2.dp.toPx(), center = Offset(lastX, lastY))
    }
}

// ---------------------------------------------------------------------------------------------
// Stat card / recent price row
// ---------------------------------------------------------------------------------------------

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecentPriceRow(point: PricePoint, delta: Double) {
    val isPositive = delta >= 0
    val color = if (isPositive) financialGreen else financialRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = point.dateKey.toShortLabel(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = point.price.asCurrency(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", delta)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.End
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Loading / error states
// ---------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MetalLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContainedLoadingIndicator(modifier = Modifier.size(72.dp))
            Text(
                text = "Loading price history...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MetalErrorState(error: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = financialRed,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Failed to load price data",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            IconButton(
                onClick = onRetry,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .padding(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White)
            }
        }
    }
}