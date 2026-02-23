package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.pizza.compose.veryBerry
import com.pizza.psx.R
import com.pizza.psx.domain.model.IndexData
import com.pizza.psx.domain.model.IndexDetailModel
import com.pizza.psx.domain.model.IndexPriceModel
import com.pizza.psx.domain.model.KLineModel
import com.pizza.psx.domain.model.SectorName
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
                            isRefreshing = isRefreshing
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
            ContainedLoadingIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading_index_data),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun ContentLoadedState(
    uiState: IndexDetailUiState,
    listState: LazyListState,
    onTickerClick: (String) -> Unit,
    displayIndexName: String,
    chartStocks: List<IndexDetailModel>,
    onRefresh: () -> Unit,
    indexPriceHistory: KLineModel,
    isRefreshing: Boolean
) {


    // Calculate total stocks count
    val totalStocks = uiState.listOfStocks?.size ?: 0

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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp),
            ) {
                LineChart(data = indexPriceHistory)
            }

        }

        item{
            Spacer(modifier = Modifier.padding(top = 8.dp))
        }
item{

    IndexChartSection(stocks = chartStocks)

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
private fun LineChart(data: KLineModel){

    val lineColor = veryBerry
    val columnColor = baraRed

    if (data.data.isEmpty())  return

    // Sort data chronologically
    val sortedData = remember(data) {
        data.data.sortedBy { it.timestamp }
    }

    val xValues = remember(sortedData) { sortedData.indices.map { it.toFloat() } }
    val indexPrice = remember(sortedData) { sortedData.map { it.close } }
    val indexVolume = remember(sortedData) { sortedData.map { it.volume } }
    val indexDate = remember(sortedData) { sortedData.map { formatShortDate(it.timestamp) } }

    val lineProducer = remember { CartesianChartModelProducer() }

    val minPrice = indexPrice.minOrNull() ?: 0.0
    val maxPrice = indexPrice.maxOrNull() ?: 0.0
    val padding = (maxPrice - minPrice) * 0.1

    val rangeProvider = remember(minPrice, maxPrice) {
        CartesianLayerRangeProvider.fixed(
            minY = minPrice - padding,
            maxY = maxPrice + padding
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
                    series(xValues,indexVolume)
                }
            }catch (e: Exception){
                lineSeries {
                    series(xValues,indexPrice)
                }
                columnSeries {
                    series(xValues,indexVolume)
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
                guideline = null,
                valueFormatter = priceFormatter,
                label = rememberTextComponent(
                    color = Color.Black,
                    textSize = 0.sp
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null,
                valueFormatter = dateFormatter,
                labelRotationDegrees = 45f,
                label = rememberTextComponent(
                    color = Color.Black,
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
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Sector Distribution",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(6.dp),
        ) {
            DonutChartWithLegend(
                data = chartData,
                isShowCentralContent = false,
                isShowTextInCenter = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}