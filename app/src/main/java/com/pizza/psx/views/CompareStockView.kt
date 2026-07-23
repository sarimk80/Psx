package com.pizza.psx.views

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.psx.R
import com.pizza.psx.domain.model.SymbolDetail
import com.pizza.psx.domain.model.TickerData
import com.pizza.psx.presentation.helpers.StockFabShape
import com.pizza.psx.presentation.viewModel.CompareStockViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareStockView(
    onBackClick: () -> Unit
) {
    val viewModel: CompareStockViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    val tickerUiState by viewModel.tickerUiState
    val selectedTickers by viewModel.selectedTickers

    var showBottomSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) { viewModel.getSymbolList() }

    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet) searchQuery = ""
    }

    val filteredSymbols by remember(searchQuery, uiState.symbolList) {
        derivedStateOf {
            val all = uiState.symbolList?.data.orEmpty()
            if (searchQuery.isBlank()) all
            else all.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Column {
                        Text(
                            text = "Compare Stocks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        AnimatedVisibility(
                            visible = selectedTickers.isNotEmpty(),
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Text(
                                text = "${selectedTickers.size} selected",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(bottom = 72.dp)
                    .navigationBarsPadding()
                    .shadow(8.dp),
                onClick = { showBottomSheet = true },
                shape = StockFabShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Add stock to compare",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Empty state
            AnimatedVisibility(
                visible = selectedTickers.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmptyStateView()
            }

            // Selected tickers chips with animation
            AnimatedVisibility(
                visible = selectedTickers.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedTickers.size) { index ->
                            val ticker = selectedTickers[index]
                            AssistChip(
                                onClick = { viewModel.removeFilterTicker(ticker) },
                                label = {
                                    Text(
                                        ticker,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Remove $ticker",
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                border = AssistChipDefaults.assistChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    enabled = true
                                )
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            // Main content
            Box(modifier = Modifier.weight(1f)) {
                if (tickerUiState.isEmpty()) {
                    CompareLoadingState()
                } else {
                    val columns = tickerUiState.map { item ->
                        CompareColumnData(
                            ticker = item.ticker?.data,
                            detail = item.symbolDetail
                        )
                    }
                    CompareTable(columns)
                }
            }

            // Bottom sheet
            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxWidth(),
                    sheetState = sheetState,
                    onDismissRequest = {
                        showBottomSheet = false
                        viewModel.getTickerDetail()
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    dragHandle = { BottomSheetDefaults.DragHandle() },
                    tonalElevation = 4.dp
                ) {
                    BottomSheetContent(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onDismiss = {
                            showBottomSheet = false
                            viewModel.getTickerDetail()
                        },
                        onClearSearch = { searchQuery = "" },
                        isLoading = uiState.symbolList == null,
                        filteredSymbols = filteredSymbols,
                        selectedTickers = selectedTickers,
                        onSymbolClick = { symbol ->
                            viewModel.addRemoveTicker(symbol)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.size(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.undraw_no_data_ig65),
                        contentDescription = null,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No stocks to compare",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap the search button to add stocks\nand start comparing their performance",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}

@Composable
private fun CompareLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading stock data...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BottomSheetContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onClearSearch: () -> Unit,
    isLoading: Boolean,
    filteredSymbols: List<String>,
    selectedTickers: List<String>,
    onSymbolClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add Stocks",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            FilledTonalIconButton(
                onClick = onDismiss,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp),
            placeholder = { Text("Search by symbol or name") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear search",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredSymbols.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredSymbols, key = { it }) { symbol ->
                        val isSelected = selectedTickers.contains(symbol)
                        SymbolListItem(
                            symbol = symbol,
                            onClick = { onSymbolClick(symbol) },
                            isSelected = isSelected
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SymbolListItem(
    symbol: String,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp)),
        onClick = onClick,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Rounded.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(6.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Keep existing data classes and compare table implementation...
private data class CompareColumnData(
    val ticker: TickerData?,
    val detail: SymbolDetail?
)

@Composable
private fun CompareTable(columns: List<CompareColumnData>) {
    val horizontalScrollState = rememberScrollState()
    val labelColumnWidth = 140.dp
    val tickerColumnWidth = 130.dp
    val rowHeight = 52.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Pinned header with cards
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .width(labelColumnWidth)
                        .height(rowHeight),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Metric",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                    columns.forEach { column ->
                        Card(
                            modifier = Modifier
                                .width(tickerColumnWidth)
                                .height(rowHeight)
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val data = column.ticker
                                if (data == null) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = data.symbol,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Scrollable body
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            compareSections.forEachIndexed { sectionIndex, section ->
                SectionHeader(
                    title = section.title,
                    modifier = Modifier
                        .background(
                            if (sectionIndex % 2 == 0)
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.surface
                        )
                )

                section.metrics.forEachIndexed { metricIndex, metric ->
                    val backgroundColor = if (metricIndex % 2 == 0)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(labelColumnWidth)
                                .height(rowHeight),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = metric.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                            columns.forEach { column ->
                                Box(
                                    modifier = Modifier
                                        .width(tickerColumnWidth)
                                        .height(rowHeight)
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val value = metric.valueOf(column)
                                    if (value == null) {
                                        Text(
                                            "—",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        val changeValue = metric.changeValueOf?.invoke(column)
                                        val textColor = when {
                                            metric.isChangeMetric && changeValue != null -> {
                                                when {
                                                    changeValue > 0 -> Color(0xFF1B8A3D) // Green
                                                    changeValue < 0 -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            }
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }

                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = when {
                                                metric.isChangeMetric && changeValue != null && changeValue > 0 ->
                                                    Color(0xFF1B8A3D).copy(alpha = 0.1f)
                                                metric.isChangeMetric && changeValue != null && changeValue < 0 ->
                                                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                                else -> Color.Transparent
                                            }
                                        ) {
                                            Text(
                                                text = value,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (metric.isChangeMetric) FontWeight.SemiBold else FontWeight.Normal,
                                                color = textColor,
                                                maxLines = 2,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (metricIndex < section.metrics.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
}

private data class CompareMetric(
    val label: String,
    val valueOf: (CompareColumnData) -> String?,
    val isChangeMetric: Boolean = false,
    val changeValueOf: ((CompareColumnData) -> Double)? = null
)

private data class CompareSection(
    val title: String,
    val metrics: List<CompareMetric>
)

private val compareSections = listOf(
    CompareSection(
        title = "QUOTE",
        metrics = listOf(
            CompareMetric("Price", { c -> c.ticker?.let { "Rs. ${"%.2f".format(it.price)}" } }),
            CompareMetric(
                "Change", { c -> c.ticker?.let { "${if (it.change >= 0) "+" else ""}${"%.2f".format(it.change)}" } },
                isChangeMetric = true, changeValueOf = { it.ticker?.change ?: 0.0 }
            ),
            CompareMetric(
                "Change %", { c -> c.ticker?.let { formatPercent(it.changePercent) } },
                isChangeMetric = true, changeValueOf = { it.ticker?.changePercent ?: 0.0 }
            ),
            CompareMetric("Volume", { c -> c.ticker?.let { formatCompact(it.volume.toDouble()) } }),
            CompareMetric("Value (Turnover)", { c -> c.ticker?.let { "Rs. ${formatCompact(it.value)}" } }),
            CompareMetric("Day Range", { c -> c.ticker?.day_range }),
            CompareMetric("52-Week Range", { c -> c.ticker?.week_range_52 }),
            CompareMetric("LDCP", { c -> c.ticker?.let { "%.2f".format(it.ldcp) } }),
        )
    ),
    CompareSection(
        title = "VALUATION",
        metrics = listOf(
            CompareMetric("Market Cap", { c -> c.ticker?.let { "Rs. ${formatCompact(it.market_cap)}" } }),
            CompareMetric("P/E Ratio", { c -> c.ticker?.let { "%.2f".format(it.p_e_ratio) } }),
            CompareMetric(
                "YTD Change", { c -> c.ticker?.let { formatPercent(it.ytd_change) } },
                isChangeMetric = true, changeValueOf = { it.ticker?.ytd_change ?: 0.0 }
            ),
            CompareMetric(
                "1-Year Change", { c -> c.ticker?.let { formatPercent(it.year_1_change) } },
                isChangeMetric = true, changeValueOf = { it.ticker?.year_1_change ?: 0.0 }
            ),
        )
    ),
    CompareSection(
        title = "FINANCIALS (LATEST ANNUAL)",
        metrics = listOf(
            CompareMetric("Sales", { c ->
                c.detail?.financials?.annual?.lastOrNull()?.sales?.let { "Rs. ${formatCompact(it.toDouble())}" }
            }),
            CompareMetric("Profit After Tax", { c ->
                c.detail?.financials?.annual?.lastOrNull()?.profitAfterTax?.let { "Rs. ${formatCompact(it.toDouble())}" }
            }),
            CompareMetric("EPS", { c ->
                c.detail?.financials?.annual?.lastOrNull()?.eps?.let { "%.2f".format(it) }
            }),
        )
    ),
    CompareSection(
        title = "RATIOS (LATEST)",
        metrics = listOf(
            CompareMetric("Gross Profit Margin", { c ->
                c.detail?.ratios?.lastOrNull()?.grossProfitMargin?.let { formatPercent(it) }
            }),
            CompareMetric("Net Profit Margin", { c ->
                c.detail?.ratios?.lastOrNull()?.netProfitMargin?.let { formatPercent(it) }
            }),
            CompareMetric(
                "EPS Growth", { c -> c.detail?.ratios?.lastOrNull()?.epsGrowth?.let { formatPercent(it) } },
                isChangeMetric = true,
                changeValueOf = { it.detail?.ratios?.lastOrNull()?.epsGrowth ?: 0.0 }
            ),
            CompareMetric("PEG", { c -> c.detail?.ratios?.lastOrNull()?.peg?.let { "%.2f".format(it) } }),
        )
    ),
    CompareSection(
        title = "LATEST ANNOUNCEMENT",
        metrics = listOf(
            CompareMetric("Date", { c -> c.detail?.announcements?.firstOrNull()?.date }),
            CompareMetric("Title", { c -> c.detail?.announcements?.firstOrNull()?.title }),
        )
    ),
)

private fun formatCompact(value: Double): String {
    val absValue = abs(value)
    return when {
        absValue >= 1_000_000_000 -> "%.2fB".format(value / 1_000_000_000)
        absValue >= 1_000_000 -> "%.2fM".format(value / 1_000_000)
        absValue >= 1_000 -> "%.2fK".format(value / 1_000)
        else -> "%.2f".format(value)
    }
}

private fun formatPercent(value: Double): String {
    val sign = if (value >= 0) "+" else ""
    return "$sign${"%.2f".format(value)}%"
}