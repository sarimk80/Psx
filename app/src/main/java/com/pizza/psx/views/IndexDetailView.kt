package com.pizza.psx.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.pizza.compose.baraRed
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.compose.financialWarning
import com.pizza.compose.veryBerry
import com.pizza.psx.R
import com.pizza.psx.domain.model.IndexData
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.IndexPriceModel
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.SectorName
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.presentation.helpers.formatDate
import com.pizza.psx.presentation.helpers.formatShortDate
import com.pizza.psx.presentation.helpers.formatTimestamp
import com.pizza.psx.presentation.helpers.formatVolume
import com.pizza.psx.presentation.helpers.getColorFromIndex
import com.pizza.psx.presentation.helpers.number_format
import com.pizza.psx.presentation.helpers.stringToIndexString
import com.pizza.psx.presentation.viewModel.IndexDetailUiState
import com.pizza.psx.presentation.viewModel.IndexDetailViewModel
import com.pizza.psx.presentation.viewModel.PortfolioUiState
import com.pizza.psx.presentation.viewModel.PortfolioViewModel
import com.pizza.psx.views.charts.ChartData
import com.pizza.psx.views.charts.DonutChartWithLegend
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexDetailView(
    indexSymbol: String,
    onBackClick: () -> Unit,
    onTickerClick: (String) -> Unit,
    viewModel: IndexDetailViewModel = hiltViewModel(),
    ticker: Ticker
) {
    val uiState by viewModel.uiState
    val indexUiState by viewModel.indexUiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Convert index symbol to display format
    val displayIndexName = remember(indexSymbol) {
        indexSymbol.replace("_", " ").uppercase()
    }


    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            val result = snackbarHostState.showSnackbar(
                message = error,
                actionLabel = context.getString(R.string.retry),
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.getIndexDetail(indexName = stringToIndexString(indexSymbol))
            }
        }
    }

    // Optional: Pull to refresh functionality
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = displayIndexName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                  uiState.isLoading && uiState.listOfStocks.isNullOrEmpty() -> {
                        FullScreenLoading()
                    }

                    (uiState.error != null && uiState.listOfStocks.isNullOrEmpty() || indexUiState.error != null) -> {
                        ErrorStateIndex(
                            error = uiState.error!!,
                            onRetry = {
                                viewModel.getIndexDetail(indexName = stringToIndexString(indexSymbol))
                            }
                        )
                    }

                    !uiState.listOfStocks.isNullOrEmpty() -> {
                        ContentLoadedState(
                            uiState = uiState,
                            listState = listState,
                            onTickerClick = onTickerClick,
                            displayIndexName = displayIndexName,
                            chartStocks = indexUiState.listOfStocks!!,
                            indexPriceHistory = indexUiState.indexPrice!!,
                            onRefresh = {
                                isRefreshing = true
                                coroutineScope.launch {
                                    viewModel.getIndexDetail(indexName = stringToIndexString(indexSymbol))
                                    delay(1000) // Minimum loading time for better UX
                                    isRefreshing = false
                                }
                            },
                            isRefreshing = isRefreshing,
                            ticker = ticker
                        )
                    }

                    else -> {
                        EmptyState(
                            onRefresh = {
                                viewModel.getIndexDetail(indexName = stringToIndexString(indexSymbol))
                            }
                        )
                    }
                }

            }
        }
    )
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ContainedLoadingIndicator(
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading_index_data),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ErrorStateIndex(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.error_loading_data),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyState(
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_data_available),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tap_to_refresh),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.material3.OutlinedButton(onClick = onRefresh) {
                Text(stringResource(R.string.refresh))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentLoadedState(
    uiState: IndexDetailUiState,
    listState: LazyListState,
    onTickerClick: (String) -> Unit,
    displayIndexName: String,
    chartStocks: List<IndexDetailModel>,
    onRefresh: () -> Unit,
    indexPriceHistory: KLineModel,
    isRefreshing: Boolean,
    ticker: Ticker
) {

    var showChartBottomSheet by remember { mutableStateOf(false) }
    var showSectorBottomSheet by remember { mutableStateOf(false) }
    var showDetailBottomSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )




    // Calculate total stocks count
    val totalStocks = uiState.listOfStocks?.size ?: 0

    if (showChartBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChartBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {

                // Header
                Text(
                    text = "Index Activity",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "Price movement over time",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                    LineChart(
                        data = indexPriceHistory,
                    )


                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showSectorBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSectorBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {

                // Header
                Text(
                    text = "Sector Distribution",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "Index weight by industry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))


                    IndexChartSection(
                        stocks = chartStocks,

                    )


                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if(showDetailBottomSheet){
        ModalBottomSheet(
            onDismissRequest = { showDetailBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ){
            TickerDetails(ticker)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with stats
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = displayIndexName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        item{
            IndexOverviewCards(
                onOpenChart = {showChartBottomSheet = true},
                onOpenDetails = {showDetailBottomSheet = true},
                onOpenSectors = {showSectorBottomSheet = true}
            )
        }
        item{
            Spacer(modifier = Modifier.padding(top = 16.dp))
        }

        // Stocks list header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Constituent Stocks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Stocks list
        items(uiState.listOfStocks ?: emptyList()) { item ->
            CompactWatchlistItemCard(
                isHideVolume = false,
                item = item,
                onRemove = {  }, // Remove functionality not needed for index view
                onTickerClick = { onTickerClick(item.data.symbol) },
                onUpdateTicker = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

}

@Composable
fun TickerDetails(
    ticker: Ticker,
    modifier: Modifier = Modifier
) {
    val data = ticker.data
    val changeColor = when {
        data.change > 0 -> financialGreen
        data.change < 0 -> financialRed
        else -> MaterialTheme.colorScheme.onSurface
    }

    val formattedTime = formatDate(ticker.timestamp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header Card ──────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Symbol + Market + Type badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = data.symbol,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
//                        if (data.sectorName.isNotBlank()) {
//                            Text(
//                                text = data.sectorName,
//                                style = MaterialTheme.typography.labelMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = data.st,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = data.market,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price + Change
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = number_format(data.price),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (data.change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = changeColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (data.change > 0) "+" else ""}${"%.2f".format(data.change)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = changeColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${if (data.changePercent > 0) "+" else ""}${"%.2f".format(data.changePercent)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = changeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // LDCP
                Text(
                    text = "LDCP: ${number_format(data.ldcp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Last updated: $formattedTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Circuit Breaker
                if (data.circuit_breaker.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = financialWarning.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = financialWarning,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Circuit Breaker: ${data.circuit_breaker}",
                                style = MaterialTheme.typography.labelSmall,
                                color = financialWarning
                            )
                        }
                    }
                }
            }
        }

        // ── Range Card ───────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ranges",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RangeRow(label = "Day Range", value = data.day_range)
                RangeRow(label = "52-Week Range", value = data.week_range_52)
            }
        }

        // ── Performance Card ─────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Performance",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PerformanceChip(
                        label = "YTD",
                        value = data.ytd_change,
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceChip(
                        label = "1-Year",
                        value = data.year_1_change,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── Stats Grid ───────────────────────────────────────────────
        val stats = buildList {
            add("High" to number_format(data.high))
            add("Low" to number_format(data.low))
            add("Volume" to formatVolume(data.volume))
            if (data.trades > 0) add("Trades" to formatVolume(data.trades))
            if (data.value > 0) add("Value" to formatVolume(data.value.toLong()))
            if (data.bid > 0) add("Bid" to number_format(data.bid))
            if (data.ask > 0) add("Ask" to number_format(data.ask))
            if (data.bidVol > 0) add("Bid Vol" to formatVolume(data.bidVol))
            if (data.askVol > 0) add("Ask Vol" to formatVolume(data.askVol))
            if (data.price_earning > 0) add("P/E Ratio" to "%.2f".format(data.price_earning))
            if (data.haircut > 0) add("Haircut" to "%.2f%%".format(data.haircut))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 800.dp)
        ) {
            items(stats) { stat ->
                CustomStatItem(label = stat.first, value = stat.second)
            }
        }
    }
}

// ── Helper Composables ────────────────────────────────────────────────────────

@Composable
private fun RangeRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PerformanceChip(
    label: String,
    value: Double,
    modifier: Modifier = Modifier
) {
    val isUp = value >= 0
    val color = if (isUp) financialGreen else financialRed
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isUp) "+" else ""}${"%.2f".format(value)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CustomStatItem(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
fun IndexChartStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp), // Slightly higher elevation for depth
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer, // Use container variant for subtle distinction
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) // Soft border for definition
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), // More balanced padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium, // Slightly larger for readability
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing for separation

            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge, // More prominent value
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary // Accent color for emphasis
            )
        }
    }
}

@Composable
private fun LineChart(data: KLineModel){

    val lineColor = veryBerry
    val columnColor = baraRed

    if (data.data.isEmpty())  return

    // Sort data chronologically
    val sortedData = remember(data) {
        data.data.sortedBy { it.timestamp }
    }

    val latest = sortedData.lastOrNull()

    val latestPrice = latest?.close
    val latestVolume = latest?.volume
    val latestDate = latest?.timestamp?.let {
        formatShortDate(it)
    } ?: "--"

    val xValues = remember(sortedData) { sortedData.indices.map { it.toFloat() } }
    val indexPrice = remember(sortedData) { sortedData.map { it.close } }
    val indexVolume = remember(sortedData) { sortedData.map { it.volume } }
    val indexDate = remember(sortedData) { sortedData.map { formatShortDate(it.timestamp) } }

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

    val minPrice = indexPrice.minOrNull() ?: 0.0
    val maxPrice = indexPrice.maxOrNull() ?: 0.0
    val padding = (maxPrice - minPrice) * 0.1

    val rangeProvider = remember(minPrice, maxPrice) {
        CartesianLayerRangeProvider.fixed(
            minY = minPrice * 0.98,
            maxY = maxPrice * 1.02
        )
    }


    val zoomState = rememberVicoZoomState(
        initialZoom = Zoom.fixed(3f),
        minZoom = Zoom.fixed(2f),   // zoom in 2x
        maxZoom = Zoom.fixed(10f)
    )


    LaunchedEffect(sortedData) {
        lineProducer.runTransaction {
            try {
                lineSeries {
                    series(xValues,indexPrice)
                }
                columnSeries {
                    series(xValues,normalizedVolumeValues)
                }
            }catch (e: Exception){
                lineSeries {
                    series(xValues,indexPrice)
                }
                columnSeries {
                    series(xValues,normalizedVolumeValues)
                }
            }

        }

    }

    // Format X-axis
    val dateFormatter = remember(indexDate) {
        CartesianValueFormatter { _, value, _ ->
            indexDate[value.toInt().coerceIn(0, indexDate.lastIndex)]
        }
    }

    // Price formatting
    val priceFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            number_format(value)
        }
    }

    val labelFormatter = remember {
        CartesianValueFormatter { _, value, _ -> formatVolume(value) }
    }

    val scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End)

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


    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            IndexChartStatCard(
                title = "Price",
                value = if(selectedXValue != 0.0 || selectedYValue != 0.0) number_format(selectedYValue)
                else number_format(latestPrice?:0.0),
                modifier = Modifier.weight(1f)
            )

            IndexChartStatCard(
                title = "Volume",
                value = if(selectedXValue != 0.0 || selectedYValue != 0.0) formatVolume(indexVolume[selectedXValue.toInt()].toLong())
                else formatVolume(latestVolume?:1000),
                modifier = Modifier.weight(1f)
            )

            IndexChartStatCard(
                title = "Date",
                value = if(selectedXValue != 0.0 || selectedYValue != 0.0) indexDate[selectedXValue.toInt()] else latestDate,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CartesianChartHost(
            scrollState = scrollState,
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    rangeProvider = rangeProvider,
                    lineProvider = LineCartesianLayer.LineProvider.series(

                        LineCartesianLayer.Line(
                            pointConnector = LineCartesianLayer.PointConnector.cubic(0.6f),
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
            //zoomState = zoomState,
            modifier = Modifier
                .height(350.dp)
                .padding(6.dp),
        )

    }
}
@Composable
private fun IndexChartSection(
    stocks: List<IndexDetailModel>
) {

    val sectorCount = remember(stocks) {
        stocks.groupBy { it.sector }
            .map { (sectorName, tickers) ->
                SectorName(sectorName, tickers.size)
            }
    }

    val chartData = remember(sectorCount) {
        sectorCount.mapIndexed { index, sector ->
            ChartData(
                label = sector.sectorName.ifEmpty { "Unknown" },
                value = sector.sectorCount.toFloat(),
                color = getColorFromIndex(index),
                price = sectorCount.size.toFloat()
            )
        }
    }

    if (chartData.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {


//        Card(
//            modifier = Modifier
//                .fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//            elevation = CardDefaults.cardElevation(6.dp),
//        ) {
            DonutChartWithLegend(
                data = chartData,
                isShowCentralContent = false,
                isShowTextInCenter = true
            )
        //}

        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
fun IndexOverviewCards(
    onOpenChart: () -> Unit,
    onOpenSectors: () -> Unit,
    onOpenDetails: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // BIG CARD → Chart
        IndexBigCard(
            title = "Index Activity",
            subtitle = "Current Trend",
            icon = Icons.Default.BarChart,
            onClick = onOpenChart,
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            IndexSmallCard(
                title = "Sector Weight",
                subtitle = "Distribution",
                icon = Icons.Default.PieChart,
                onClick = onOpenSectors
            )

            IndexSmallCard(
                title = "Index Stats",
                subtitle = "Market Snapshot",
                icon = Icons.Default.Analytics,
                onClick = onOpenDetails
            )
        }
    }
}

@Composable
fun IndexBigCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 60.dp
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Box takes all remaining space above the text
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Text section at the bottom
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun IndexSmallCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.width(12.dp))

            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}