package com.example.psx.views

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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.psx.domain.model.Ticker
import com.example.psx.presentation.viewModel.TickerDetailViewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.compose.financialGreen
import com.example.compose.financialRed
import com.example.psx.domain.model.MarketDividend
import com.example.psx.presentation.helpers.number_format
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val viewModel: TickerDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getMarketDividend()
        while (true){
            viewModel.getTickerDetailAll(type = "IDC", symbol = listOf("KSE100","ALLSHR","KMI30","PSXDIV20","KSE30","MII30"))
            delay(70_000)
        }
    }

    Scaffold(topBar = { TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Market Indices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
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
                        viewModel.getTickerDetailAll(type = "IDC", symbol = listOf("KSE100","ALLSHR","KMI30","PSXDIV20","KSE30","MII30"))
                    },
                    modifier = Modifier.weight(1f)
                )
                !uiState.listOfTicker.isNullOrEmpty() -> HomeContent(
                    tickers = uiState.listOfTicker!!,
                    marketDividends = uiState.marketDividend,
                    isDividendLoading = uiState.isDividendLoading,
                    modifier = Modifier.weight(1f)
                )
                else -> HomeErrorState(
                    error = "No data available",
                    onRetry = {
                        viewModel.getTickerDetailAll(type = "IDC", symbol = listOf("KSE100","ALLSHR","KMI30","PSXDIV20","KSE30","MII30"))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    tickers: List<Ticker>,
    marketDividends: List<MarketDividend>?,
    isDividendLoading: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            TickerHorizontalPager(tickers = tickers)
        }

        item {
            MarketDividendSection(
                marketDividends = marketDividends,
                isLoading = isDividendLoading
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TickerHorizontalPager(
    tickers: List<Ticker>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { tickers.size }

    Column(modifier = modifier) {
        // Pager takes fixed height (approximately half screen)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp) // Fixed height instead of weight
        ) { page ->
            TickerPage(ticker = tickers[page])
        }

        // Page indicators
        DotsIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

    }
}

@Composable
fun MarketDividendSection(
    marketDividends: List<MarketDividend>?,
    isLoading: Boolean
) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dividend", "EPS", "Meeting", "Profits")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {


        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> DividendLoadingState()
            marketDividends.isNullOrEmpty() -> DividendEmptyState()
            else -> FilteredDividendList(
                dividends = marketDividends,
                selectedTab = selectedTab
            )
        }
    }
}

@Composable
fun FilteredDividendList(
    dividends: List<MarketDividend>,
    selectedTab: Int
) {
    val filteredDividends = remember(dividends, selectedTab) {
        when (selectedTab) {
            0 -> dividends.filter { it.Dividend != "-" && it.Dividend.isNotEmpty() }
            1 -> dividends.filter { it.Eps != "-" && it.Eps.isNotEmpty() }
            2 -> dividends.filter { it.BoardMeeting != "-" && it.BoardMeeting.isNotEmpty() }
            3 -> dividends.filter {
                it.profitLossBeforeTax != "-" && it.profitLossBeforeTax.isNotEmpty() &&
                        it.profitLossAfterTax != "-" && it.profitLossAfterTax.isNotEmpty()
            }
            else -> dividends
        }
    }

    if (filteredDividends.isEmpty()) {
        NoDataForTabState(selectedTab = selectedTab)
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            items(filteredDividends.take(20)) { dividend ->
                MarketDividendItem(
                    dividend = dividend,
                    selectedTab = selectedTab
                )
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Company and Icon
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

            // Tab-specific content
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
        InfoRow("Year Ended", dividend.yearEnded,)

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
fun InfoRow(label: String, value: String, isPositive: Boolean? = null,isMeeting: Boolean? = null) {
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

// Helper function to check if a value is positive or negative
private fun isPositiveValue(value: String): Boolean? {
    if (value == "-" || value.isEmpty()) return null

    return try {
        // Check if value contains parentheses - indicates negative value
        val isNegative = value.contains("(") && value.contains(")")

        if (isNegative) {
            false // Parentheses indicate loss/negative value
        } else {
            // Remove commas and spaces, then check if positive
            val cleanValue = value.replace(",", "").replace(" ", "")
            val numericValue = cleanValue.toDoubleOrNull()
            numericValue?.let { it > 0 } ?: true // Assume positive if can't parse but no parentheses
        }
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
fun TickerPage(ticker: Ticker) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            // =====================
            // Header Section
            // =====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Symbol + Tag
                Column {
                    Text(
                        text = ticker.data.symbol,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Market Data",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Price + Change
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = number_format(ticker.data.price),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isUp = ticker.data.change >= 0
                        Icon(
                            imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isUp) financialGreen else financialRed,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${"%.2f".format(ticker.data.change)} (${"%.2f".format(ticker.data.changePercent)}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUp) financialGreen else financialRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =====================
            // Stats Grid
            // =====================
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    StatItem(
                        label = "High",
                        value = number_format(ticker.data.high),
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF00C853)
                    )
                }
                item {
                    StatItem(
                        label = "Low",
                        value = number_format(ticker.data.low),
                        icon = Icons.Default.TrendingDown,
                        color = Color(0xFFD32F2F)
                    )
                }
                item {
                    StatItem(
                        label = "Volume",
                        value = formatVolume(ticker.data.volume),
                        icon = Icons.Default.BarChart,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    StatItem(
                        label = "Trades",
                        value = formatNumber(ticker.data.trades),
                        icon = Icons.Default.SwapHoriz,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            //Spacer(modifier = Modifier.height(8.dp))
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ContainedLoadingIndicator()
            Text(
                "Loading...",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun HomeErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

// Helper functions for formatting
private fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000 -> "%.2fM".format(volume / 1_000_000.0)
        volume >= 1_000 -> "%.2fK".format(volume / 1_000.0)
        else -> volume.toString()
    }
}

private fun formatNumber(number: Long): String {
    return String.format("%,d", number)
}