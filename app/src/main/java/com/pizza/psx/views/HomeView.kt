package com.pizza.psx.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.presentation.viewModel.TickerDetailViewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.pizza.compose.financialGreen
import com.pizza.compose.financialGrey
import com.pizza.compose.financialRed
import com.pizza.compose.financialWarning
import com.pizza.compose.veryBlue
import com.pizza.psx.domain.model.MarketDividend
import com.pizza.psx.presentation.helpers.marketStatus
import com.pizza.psx.presentation.helpers.number_format
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onIndexClick:(String, Ticker) -> Unit = { _ ,_ -> }
) {
    val viewModel: TickerDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    var currentMarketStatus by remember { mutableStateOf("OPN") }

    LaunchedEffect(Unit) {
        viewModel.getMarketDividend()
        while (true){
            viewModel.getTickerDetailAll(type = "IDC", symbol = listOf("KSE100","KMI30","PSXDIV20","KSE30","MII30"))
            delay(90_000)
        }
    }

    LaunchedEffect(uiState.listOfTicker) {
        uiState.listOfTicker?.firstOrNull()?.let { ticker ->
            currentMarketStatus = ticker.data.st
        }
    }

    val todayDate = remember {
        java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
    }

    Scaffold(topBar = { TopAppBar(
        title = {
            Column {
                Text(
                    text = "Market Indices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${marketStatus(currentMarketStatus)} . $todayDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> HomeLoadingState(modifier = Modifier.weight(1f))
                uiState.error != null -> HomeErrorState(
                    error = uiState.error!!,
                    onRetry = {
                        viewModel.getTickerDetailAll(type = "IDC", symbol = listOf("KSE100","KMI30","PSXDIV20","KSE30","MII30"))
                    },
                    modifier = Modifier.weight(1f)
                )
                !uiState.listOfTicker.isNullOrEmpty() -> HomeContent(
                    tickers = uiState.listOfTicker!!,
                    marketDividends = uiState.marketDividend,
                    isDividendLoading = uiState.isDividendLoading,
                    modifier = Modifier.weight(1f),
                    onIndexClick = onIndexClick
                )
                else -> HomeErrorState(
                    error = "No data available",
                    onRetry = {
                        viewModel.getTickerDetailAll(type = "IDC", symbol = listOf("KSE100","KMI30","PSXDIV20","KSE30","MII30"))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─── KEY CHANGE ──────────────────────────────────────────────────────────────
// HomeContent is now a single LazyColumn that owns the whole scroll.
// The tab row is hoisted as a stickyHeader so it pins when scrolled past the pager.
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    tickers: List<Ticker>,
    marketDividends: List<MarketDividend>?,
    isDividendLoading: Boolean,
    modifier: Modifier = Modifier,
    onIndexClick: (String, Ticker) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dividend", "EPS", "Meeting", "Profits")

    // Pre-compute filtered list so stickyHeader and items stay in sync
    val filteredDividends = remember(marketDividends, selectedTab) {
        marketDividends?.let { list ->
            when (selectedTab) {
                0 -> list.filter { it.Dividend != "-" && it.Dividend.isNotEmpty() }
                1 -> list.filter { it.Eps != "-" && it.Eps.isNotEmpty() }
                2 -> list.filter { it.BoardMeeting != "-" && it.BoardMeeting.isNotEmpty() }
                3 -> list.filter {
                    it.profitLossBeforeTax != "-" && it.profitLossBeforeTax.isNotEmpty() &&
                            it.profitLossAfterTax != "-" && it.profitLossAfterTax.isNotEmpty()
                }
                else -> list
            }
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {

        // ── 1. Horizontal pager with dot indicators ──────────────────────────
        item {
            TickerHorizontalPager(tickers = tickers, onIndexClick = onIndexClick)
        }

        // ── 2. Sticky tab row ────────────────────────────────────────────────
        stickyHeader {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    // Give it a solid background so content doesn't bleed through
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        width = 32.dp,
                        height = 3.dp
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }

        // ── 3. Dividend / tab content items ─────────────────────────────────
        item { Spacer(modifier = Modifier.height(12.dp)) }

        when {
            isDividendLoading -> item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) { DividendLoadingState() }
            }

            marketDividends.isNullOrEmpty() -> item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) { DividendEmptyState() }
            }

            filteredDividends.isNullOrEmpty() -> item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    NoDataForTabState(selectedTab = selectedTab)
                }
            }

            else -> {
                items(filteredDividends) { dividend ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MarketDividendItem(
                            dividend = dividend,
                            selectedTab = selectedTab
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Everything below is unchanged from the original
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TickerHorizontalPager(
    tickers: List<Ticker>,
    modifier: Modifier = Modifier,
    onIndexClick: (String, Ticker) -> Unit
) {
    val pagerState = rememberPagerState { tickers.size }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) { page ->
            TickerPage(ticker = tickers[page], onClick = onIndexClick)
        }

        DotsIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DividendLoadingState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ContainedLoadingIndicator()
            Text(
                "Loading dividends...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun DividendEmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AttachMoney,
                contentDescription = "No dividends",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "No dividend data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun MarketDividendItem(
    dividend: MarketDividend,
    selectedTab: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dividend.Company.take(2).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = dividend.Company,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> DividendTabContent(dividend)
                1 -> EpsTabContent(dividend)
                2 -> MeetingTabContent(dividend)
                3 -> ProfitsTabContent(dividend)
            }
        }
    }
}

@Composable
fun DividendTabContent(dividend: MarketDividend) {
    Column {
        InfoRow("Dividend", dividend.Dividend, isPositive = dividend.Dividend != "-")
        InfoRow("Date", dividend.Date, isMeeting = true)
    }
}

@Composable
fun EpsTabContent(dividend: MarketDividend) {
    Column {
        InfoRow("EPS", dividend.Eps, isPositive = isPositiveValue(dividend.Eps))
        InfoRow("Year Ended", dividend.yearEnded)
    }
}

@Composable
fun MeetingTabContent(dividend: MarketDividend) {
    Column {
        InfoRow("Board Meeting", dividend.BoardMeeting, isMeeting = true)
    }
}

@Composable
fun ProfitsTabContent(dividend: MarketDividend) {
    Column {
        InfoRow("Profit Before Tax", dividend.profitLossBeforeTax, isPositive = isPositiveValue(dividend.profitLossBeforeTax))
        InfoRow("Profit After Tax", dividend.profitLossAfterTax, isPositive = isPositiveValue(dividend.profitLossAfterTax))
    }
}

@Composable
fun InfoRow(label: String, value: String, isPositive: Boolean? = null, isMeeting: Boolean? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = when {
                isPositive == true -> financialGreen
                isPositive == false -> financialRed
                isMeeting == true -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primary
            }
        )
    }
}

@Composable
fun NoDataForTabState(selectedTab: Int) {
    val tabNames = listOf("Dividend", "EPS", "Meeting", "Profits")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "No data",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "No ${tabNames[selectedTab].lowercase()} data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Try another tab",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

private fun isPositiveValue(value: String): Boolean? {
    if (value == "-" || value.isEmpty()) return null
    return try {
        val numericPart = value
            .replace(",", "")
            .replace("standalone", "")
            .replace("consolidated", "")
            .replace("unconsolidated", "")
            .trim()

        if (numericPart == "-" || numericPart.isEmpty()) return null

        // handle bracket notation like (10.19) meaning negative
        val isBracketNegative = numericPart.startsWith("(") && numericPart.endsWith(")")
        if (isBracketNegative) return false

        val numericValue = numericPart.toDoubleOrNull()
        numericValue?.let { it > 0 }
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pagerState.pageCount) { index ->
            val color = if (pagerState.currentPage == index) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun TickerPage(ticker: Ticker, onClick: (String, Ticker) -> Unit) {
    Card(
        onClick = { onClick(ticker.data.symbol, ticker) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = ticker.data.symbol,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(2.dp))


                    Text(
                        text = marketStatus(ticker.data.st),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = number_format(ticker.data.price),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isUp = ticker.data.change >= 0
                        Icon(
                            imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isUp) financialGreen else financialRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${number_format(ticker.data.change)} (${"%.2f".format(ticker.data.changePercent)}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUp) financialGreen else financialRed
                        )
                    }
//                    Spacer(modifier = Modifier.height(2.dp))
//                    Text(
//                        text = "LDCP: ${number_format(ticker.data.ldcp)}",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.outline
//                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Day Range
            if (ticker.data.day_range.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Day Range",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = ticker.data.day_range,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // 52-Week Range
            if (ticker.data.week_range_52.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "52-Week Range",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = ticker.data.week_range_52,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Main Stats Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                item {
                    StatItem(label = "High", value = number_format(ticker.data.high), icon = Icons.Default.TrendingUp, color = financialGreen)
                }
                item {
                    StatItem(label = "Low", value = number_format(ticker.data.low), icon = Icons.Default.TrendingDown, color = financialRed)
                }
                item {
                    StatItem(label = "Close", value = number_format(ticker.data.ldcp), icon = Icons.Default.PlayArrow, color = veryBlue)
                }
                item {
                    StatItem(label = "Volume", value = formatVolume(ticker.data.volume), icon = Icons.Default.BarChart, color = financialWarning)
                }
                if (ticker.data.value > 0) {
                    item {
                        StatItem(label = "Value", value = formatVolume(ticker.data.value.toLong()), icon = Icons.Default.Payments, color = financialWarning)
                    }
                }
                if (ticker.data.trades > 0) {
                    item {
                        StatItem(label = "Trades", value = formatNumber(ticker.data.trades), icon = Icons.Default.SwapHoriz, color = financialWarning)
                    }
                }
                if (ticker.data.bid > 0) {
                    item {
                        StatItem(label = "Bid", value = number_format(ticker.data.bid), icon = Icons.Default.ArrowDownward, color = financialRed)
                    }
                }
                if (ticker.data.ask > 0) {
                    item {
                        StatItem(label = "Ask", value = number_format(ticker.data.ask), icon = Icons.Default.ArrowUpward, color = financialGreen)
                    }
                }
                if (ticker.data.year_1_change != 0.0) {
                    item {
                        val isUp = ticker.data.year_1_change >= 0
                        StatItem(
                            label = "1Y Change",
                            value = "${"%.2f".format(ticker.data.year_1_change)}%",
                            icon = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            color = if (isUp) financialGreen else financialRed
                        )
                    }
                }
                if (ticker.data.ytd_change != 0.0) {
                    item {
                        val isUp = ticker.data.ytd_change >= 0
                        StatItem(
                            label = "YTD Change",
                            value = "${"%.2f".format(ticker.data.ytd_change)}%",
                            icon = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            color = if (isUp) financialGreen else financialRed
                        )
                    }
                }
                if (ticker.data.price_earning > 0) {
                    item {
                        StatItem(label = "P/E Ratio", value = "${"%.2f".format(ticker.data.price_earning)}", icon = Icons.Default.Analytics, color = financialGrey)
                    }
                }
                if (ticker.data.haircut > 0) {
                    item {
                        StatItem(label = "Haircut", value = "${"%.2f".format(ticker.data.haircut)}%", icon = Icons.Default.ContentCut, color = financialWarning)
                    }
                }
            }

            // Circuit Breaker Badge
            if (ticker.data.circuit_breaker.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
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
                            text = "Circuit Breaker: ${ticker.data.circuit_breaker}",
                            style = MaterialTheme.typography.labelSmall,
                            color = financialWarning
                        )
                    }
                }
            }

            // Timestamp
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Updated: ${
                    java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
                        .format(java.util.Date(ticker.data.timestamp * 1000L))
                }",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeLoadingState(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ContainedLoadingIndicator(
                modifier = Modifier.size(80.dp),
            )
            Text("Loading...",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun HomeErrorState(error: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = "Error", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
            Text(text = error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 16.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000 -> "%.2fM".format(volume / 1_000_000.0)
        volume >= 1_000 -> "%.2fK".format(volume / 1_000.0)
        else -> volume.toString()
    }
}

private fun formatNumber(number: Long): String {
    return String.format("%,d", number)
}