@file:OptIn(ExperimentalMaterial3Api::class)

package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.pizza.compose.rust
import com.pizza.psx.R
import com.pizza.psx.domain.model.Companies
import com.pizza.psx.domain.model.Etf
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.KLineModelData
import com.pizza.psx.domain.model.Symbol
import com.pizza.psx.presentation.helpers.getColorFromIndex
import com.pizza.psx.presentation.viewModel.EtfDetailViewModel
import com.pizza.psx.views.charts.ChartData
import com.pizza.psx.views.charts.DonutChartWithLegend


import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.pizza.psx.presentation.helpers.formatShortDate

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.pizza.psx.presentation.helpers.formatShortDate
import com.pizza.psx.presentation.helpers.formatVolume
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.pizza.compose.baraRed
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.compose.veryBerry
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.presentation.helpers.number_format

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EtfDetailView(etfSymbol: String,
                  onBackClick: () -> Unit,
                  etfModel: Etf,
                  onTickerDetail: (ticker: String) -> Unit){

    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Overview", "Holdings")

    val viewModel: EtfDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getAllEtfData()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(etfModel.etfName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }

    ) {paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            // Tab row
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index  },
                        text = { Text(title) }
                    )
                }
            }

            when {
                uiState.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ContainedLoadingIndicator(
                        modifier = Modifier.size(80.dp)
                    )
                }
                uiState.error != null -> {
                    EtfErrorState(
                        error = uiState.error!!,
                        onRetry = { viewModel.getAllEtfData() }
                    )
                }
                        uiState.company!= null &&
                        uiState.etfModel!= null &&
                        uiState.kLine!= null &&
                        uiState.ticker != null -> {
                    when (selectedTabIndex) {
                        0 -> EtfDetailTab(etfSymbol,uiState.kLine!!,uiState.etfModel!!,uiState.company!!,uiState.ticker!!)
                        1 -> EtfStocksTab(etfSymbol,
                            uiState.etfModel!!,
                            onTickerDetail = onTickerDetail)
                    }
                }
            }


        }
    }
}

@Composable
fun EtfDetailTab(
    etfSymbol: String,
    kLineModel: KLineModel,
    etf: Etf,
    companies: Companies,
    ticker: Ticker
) {
    val isPositive = ticker.data.change >= 0
    val changeColor = if (isPositive) financialGreen else financialRed
    val changeIcon = if (isPositive) "▲" else "▼"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Hero Header ──────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = etfSymbol,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = etf.fullName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Market status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ticker.data.st,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Live price block
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = number_format(ticker.data.price),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(changeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$changeIcon ${number_format(ticker.data.change)} (${
                                "%.2f".format(ticker.data.changePercent)
                            }%)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = changeColor
                        )
                    }
                }
            }
        }

        // ── Market Data Card ─────────────────────────────────────
        item {
            SectionCard(title = "Trading Summary") {
                // Row 1: High / Low / Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarketStatItem(
                        modifier = Modifier.weight(1f),
                        label = "High",
                        value = number_format(ticker.data.high),
                        valueColor = financialGreen
                    )
                    MarketStatItem(
                        modifier = Modifier.weight(1f),
                        label = "Low",
                        value = number_format(ticker.data.low),
                        valueColor = financialRed
                    )
                    MarketStatItem(
                        modifier = Modifier.weight(1f),
                        label = "Volume",
                        value = formatVolume(ticker.data.volume)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Row 2: Bid / Ask / Trades
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarketStatItem(
                        modifier = Modifier.weight(1f),
                        label = "Bid",
                        value = number_format(ticker.data.bid),
                        subValue = formatVolume(ticker.data.bidVol),
                        subLabel = "vol"
                    )
                    MarketStatItem(
                        modifier = Modifier.weight(1f),
                        label = "Ask",
                        value = number_format(ticker.data.ask),
                        subValue = formatVolume(ticker.data.askVol),
                        subLabel = "vol"
                    )
                    MarketStatItem(
                        modifier = Modifier.weight(1f),
                        label = "Trades",
                        value = formatVolume(ticker.data.trades)
                    )
                }
            }
        }

        // ── Chart ────────────────────────────────────────────────
        item {
            SectionCard(title = "Price Performance") {
                EtfChartView(kLineModel.data)
            }
        }

        // ── Fund Metrics ─────────────────────────────────────────
        item {
            SectionCard(title = "Fund Detail") {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    FundMetricRow(label = "Market Cap", value = etf.marketCap)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    FundMetricRow(label = "Fund Size", value = etf.fundSize)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    FundMetricRow(label = "No. of Shares", value = etf.noOfShares)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    FundMetricRow(label = "Type", value = etf.type)
                }
            }
        }

        // ── Description ──────────────────────────────────────────
        item {
            var expanded by remember { mutableStateOf(false) }
            SectionCard(title = "About the fund") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = etf.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) Int.MAX_VALUE else 4,
                        overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (expanded) "Show less" else "Read more",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            }
        }

        // ── Key People ───────────────────────────────────────────
        if (etf.keyPeople.isNotEmpty()) {
            item {
                SectionCard(title = "Key People") {
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        etf.keyPeople.forEachIndexed { index, person ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Avatar circle
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = person.person.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = person.person,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = person.position,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (index < etf.keyPeople.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// ── Reusable Components ──────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            content()
        }
    }
}

@Composable
private fun MarketStatItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    subValue: String? = null,
    subLabel: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        if (subValue != null && subLabel != null) {
            Text(
                text = "$subValue $subLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun FundMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EtfStocksTab(etfSymbol: String, etf: Etf,onTickerDetail: (ticker: String) -> Unit) {

    val chartSampleData = remember(etf.symbols) {
        etf.symbols.mapIndexed { index, etfSymbols ->
            ChartData(
                label = etfSymbols.ticker,
                value = etfSymbols.weight.toFloat(),
                color = getColorFromIndex(index),
                price = etfSymbols.weight.toFloat()
            )
        }
    }

    val totalWeight = remember(etf.symbols) {
        etf.symbols.sumOf { it.weight }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Header Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = etfSymbol,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Summary Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Holdings",
                    value = "${etf.symbols.size}"
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Portfolio Weight",
                    value = "${"%.1f".format(totalWeight)}%"
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "Top Weight",
                    value = "${"%.2f".format(etf.symbols.maxOfOrNull { it.weight } ?: 0.0)}%"
                )
            }
        }

        // Donut Chart Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
                elevation = CardDefaults.cardElevation(6.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Portfolio Allocation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DonutChartWithLegend(
                        data = chartSampleData,
                        isShowCentralContent = false,
                        isShowTextInCenter = true
                    )
                }
            }
        }

        // Holdings Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Holdings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Companies included in $etfSymbol",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Holdings List
        itemsIndexed(etf.symbols) { index, holding ->
            HoldingCard(
                holding = holding,
                index = index,
                maxWeight = etf.symbols.maxOfOrNull { it.weight } ?: 1.0,
                onTickerDetail = onTickerDetail
            )
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HoldingCard(
    holding: Symbol,
    index: Int,
    maxWeight: Double,
    onTickerDetail: (ticker:String) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { onTickerDetail(holding.ticker) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Rank badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(rust.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = rust
                    )
                }

                // Ticker + Company
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = holding.ticker,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = holding.company,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Weight badge
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(rust.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${"%.2f".format(holding.weight)}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = rust
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EtfChartView(kLineModel: List<KLineModelData>,){
    if (kLineModel.isEmpty()) return

    // Sort data chronologically
    val sortedData = remember(kLineModel) {
        kLineModel.sortedBy { it.timestamp }
    }

    val xValues = remember(sortedData) { sortedData.indices.map { it.toFloat() } }
    val closeValues = remember(sortedData) { sortedData.map { it.close.toFloat() } }
    val dateList = remember(sortedData) { sortedData.map { formatShortDate(it.timestamp) } }
    val normalizedVolumeValues = remember(sortedData) {
        val minPrice = sortedData.minOfOrNull { it.low }?.toFloat() ?: 0f
        val maxPrice = sortedData.maxOfOrNull { it.high }?.toFloat() ?: 1f
        val priceRange = (maxPrice - minPrice).takeIf { it > 0f } ?: 1f

        val volumes = sortedData.map { it.volume.toFloat() }
        val minVol = volumes.minOrNull() ?: 0f
        val maxVol = volumes.maxOrNull()?.takeIf { it > minVol } ?: 1f
        val volRange = (maxVol - minVol).takeIf { it > 0f } ?: 1f

        // Normalize 0..1 then scale to bottom 20% of price range, offset by minPrice
        volumes.map { vol ->
            minPrice + ((vol - minVol) / volRange) * (priceRange * 0.20f)
        }
    }
    val lineProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(sortedData) {
        lineProducer.runTransaction {
            try {
                lineSeries {
                    series(xValues,closeValues)
                }
                columnSeries {
                    series(xValues,normalizedVolumeValues)
                }
            }catch (e: Exception){
                lineSeries {
                    series(xValues,closeValues)
                }
                columnSeries {
                    series(xValues,normalizedVolumeValues)
                }
            }

        }

    }

    val dateFormatter = remember(dateList) {
        CartesianValueFormatter { _, value, _ ->
            dateList[value.toInt().coerceIn(0, dateList.lastIndex)]
        }
    }

    // Price formatting
    val priceFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            String.format("%.2f", value)
        }
    }

    val stats = remember(sortedData) {
        val current = sortedData.last()
        val first = sortedData.first()
        val change = current.close - first.close
        val changePercent = (change / first.close) * 100

        val highest = sortedData.maxOf { it.high }
        val lowest = sortedData.minOf { it.low }
        val avgVolume = sortedData.map { it.volume }.average()

        Triple(change, changePercent, highest to lowest)
    }

    val lineColor = veryBerry
    val columnColor = baraRed

    var markerTargets by remember { mutableStateOf<List<CartesianMarker.Target>>(emptyList()) }

    var selectedXValue by remember { mutableStateOf(0.0) }
    var selectedYValue by remember { mutableStateOf(0.0) }

    val markerVisibilityListener = remember {


        fun List<CartesianMarker.Target>.lastEntry() =
            lastOrNull()?.let { target ->
                when (target) {
                    is LineCartesianLayerMarkerTarget ->
                        target.points.lastOrNull()?.entry

                    is ColumnCartesianLayerMarkerTarget ->
                        target.columns.lastOrNull()?.entry

                    else -> null
                }
            }




        object : CartesianMarkerVisibilityListener {
            override fun onShown(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                targets
                    .filterIsInstance<LineCartesianLayerMarkerTarget>()
                    .firstOrNull()
                    ?.points
                    ?.firstOrNull()
                    ?.entry
                    ?.let {
                        selectedXValue = it.x
                        selectedYValue = it.y
                    }
            }

            override fun onUpdated(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {

                targets
                    .filterIsInstance<LineCartesianLayerMarkerTarget>()
                    .firstOrNull()
                    ?.points
                    ?.firstOrNull()
                    ?.entry
                    ?.let {
                        selectedXValue = it.x
                        selectedYValue = it.y
                    }
            }

            override fun onHidden(marker: CartesianMarker) {
                selectedXValue = 0.0
                selectedYValue = 0.0
            }
        }
    }

    val rangeProvider = remember(stats) {
        CartesianLayerRangeProvider.fixed(
            minY = stats.third.second * 0.98,
            maxY = stats.third.first * 1.02
        )
    }

    val scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End)


    CartesianChartHost(
        scrollState = scrollState,
        //chart = rememberCartesianChart(),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = rangeProvider,
                lineProvider = LineCartesianLayer.LineProvider.series(

                    LineCartesianLayer.Line(
                        pointConnector = LineCartesianLayer.PointConnector.cubic(0.9f),
                        fill = remember {
                            LineCartesianLayer.LineFill.single(
                                fill(lineColor)
                            )
                        },
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill(lineColor.copy(alpha = 0.1f))
                        ),
                    )


                ),
                verticalAxisPosition = Axis.Position.Vertical.Start

            ),
            rememberColumnCartesianLayer(
                rangeProvider = rangeProvider,
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(columnColor.copy(alpha = 0.5f)),
                        thickness = 10.dp,
                        shape = CorneredShape.rounded(60)
                    )
                ),
                verticalAxisPosition = Axis.Position.Vertical.End
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = priceFormatter,
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textSize = 8.sp
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = dateFormatter,
                labelRotationDegrees = 45f,
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textSize = 8.sp
                ),
            ),
            marker = DefaultCartesianMarker(
                label = TextComponent(
                    textSizeSp = 0.0f
                )
            ),
            markerVisibilityListener = markerVisibilityListener
        ),
        modelProducer = lineProducer,

        modifier = Modifier.height(350.dp),
    )
}