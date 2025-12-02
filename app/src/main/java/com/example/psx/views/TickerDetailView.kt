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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.psx.domain.model.CompaniesData
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.financialGreen
import com.example.compose.financialGrey
import com.example.compose.financialRed
import com.example.compose.financialWarning
import com.example.psx.domain.model.DividendData
import com.example.psx.domain.model.FundamentalData
import com.example.psx.domain.model.KeyPerson
import com.example.psx.domain.model.TickerData
import com.example.psx.presentation.viewModel.TickerDetailViewModel


@Composable
fun TickerDetailView(type: String, symbol: String, onBack: () -> Unit) {
    val viewModel: TickerDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = symbol) {
        viewModel.getTickerAndCompanyDetail(type = type, symbol = symbol)
        viewModel.getKlineData(symbol = symbol)
    }

    Scaffold(
        topBar = {
            TickerDetailTopAppBar(
                symbol = symbol,
                onBack = onBack,
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> TickerLoadingState()
                uiState.error != null -> TickerErrorState(error = uiState.error!!, onRetry = {viewModel.getTickerAndCompanyDetail(type = type, symbol = symbol)})
                uiState.stocks != null && uiState.company != null &&
                        uiState.fundamentals != null && uiState.dividend != null ->
                    CombinedTickerDetailContent(
                        tickerData = uiState.stocks!!.data,
                        companyData = uiState.company!!.data,
                        fundamentalData = uiState.fundamentals!!.data,
                        dividendData = uiState.dividend!!.data
                    )
                else -> TickerLoadingState()
            }
        }
    }
}

@Composable
fun TickerLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Loading sector data...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TickerErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                text = "Failed to load sectors",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
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
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerDetailTopAppBar(
    symbol: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = symbol.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Composable
fun CombinedTickerDetailContent(
    tickerData: TickerData,
    companyData: CompaniesData,
    fundamentalData: FundamentalData,
    dividendData: List<DividendData>
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Company", "Financials", "Dividends")

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Stats Header - Always visible
        QuickStatsHeader(tickerData, fundamentalData)

        ChartView()

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        when (selectedTab) {
            0 -> OverviewTab(tickerData, companyData, fundamentalData)
            1 -> CompanyTab(companyData)
            2 -> FinancialsTab(fundamentalData)
            3 -> DividendsTab(dividendData, companyData.symbol)
        }
    }
}

@Composable
fun ChartView(){




}

@Composable
fun QuickStatsHeader(tickerData: TickerData, fundamentalData: FundamentalData) {
    val isPositive = tickerData.change >= 0
    val priceColor = if (isPositive) financialGreen else financialRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Price and Change
            Column {
                Text(
                    text = "${tickerData.price}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = priceColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (isPositive) "+" else ""}${tickerData.change} (${tickerData.changePercent}%)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = priceColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Market Cap and Sector
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Market Cap",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = fundamentalData.marketCap,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = fundamentalData.sector,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun OverviewTab(tickerData: TickerData, companyData: CompaniesData, fundamentalData: FundamentalData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { MarketInfoCard(tickerData) }
        item { TradingStatsCard(tickerData) }
        item { BidAskCard(tickerData) }
        item { FundamentalOverviewCard(fundamentalData) }
        item { CompanyOverviewCard(companyData) }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun CompanyOverviewCard(companyData: CompaniesData) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Company Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            FinancialMetricRow("Market Cap", companyData.financialStats.marketCap.raw)
            FinancialMetricRow("Shares", companyData.financialStats.shares.raw)
            FinancialMetricRow("Free Float", companyData.financialStats.freeFloat.raw)
            FinancialMetricRow("Free Float %", companyData.financialStats.freeFloatPercent.raw)
        }
    }
}

// Helper Composable Functions
@Composable
private fun FinancialMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BidAskCard(tickerData: TickerData) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bid & Ask",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bid", style = MaterialTheme.typography.titleMedium)
                    Text("${tickerData.bid}", style = MaterialTheme.typography.bodyLarge)
                    Text("Vol: ${tickerData.bidVol}", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ask", style = MaterialTheme.typography.titleMedium)
                    Text("${tickerData.ask}", style = MaterialTheme.typography.bodyLarge)
                    Text("Vol: ${tickerData.askVol}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun TradingStatsCard(tickerData: TickerData) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),

        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trading Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            TradingStatRow("Price", "${tickerData.price}")
            TradingStatRow("Change", "${"%.2f".format(tickerData.change)} (${tickerData.changePercent}%)")
            TradingStatRow("Volume", "${tickerData.volume}")
            TradingStatRow("High/Low", "${tickerData.high} / ${tickerData.low}")
            TradingStatRow("Trades", "${tickerData.trades}")
        }
    }
}

@Composable
private fun TradingStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CompanyTab(companyData: CompaniesData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { BusinessDescriptionCard(companyData) }
        item { KeyPeopleCard(companyData) }
        item { CompanyInfoCard(companyData) }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun CompanyInfoCard(companyData: CompaniesData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Company Info",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Symbol: ${companyData.symbol}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Last Updated: ${companyData.scrapedAt}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun KeyPeopleCard(companyData: CompaniesData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Key People",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            companyData.keyPeople.forEach { person ->
                KeyPersonItem(person)
            }
        }
    }
}
@Composable
private fun KeyPersonItem(person: KeyPerson) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(person.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text(person.position, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BusinessDescriptionCard(companyData: CompaniesData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Business Description",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = companyData.businessDescription,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MarketInfoCard(tickerData: TickerData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Market Information",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Market Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // High/Low Section
            HighLowSection(tickerData)

            Spacer(modifier = Modifier.height(12.dp))

            // Volume & Trades Section
            VolumeTradesSection(tickerData)

            Spacer(modifier = Modifier.height(12.dp))

            // Additional Market Data
            AdditionalMarketData(tickerData)
        }
    }
}

@Composable
fun HighLowSection(tickerData: TickerData) {
    Column {
        Text(
            text = "Daily Range",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Day High
            MarketDataItem(
                title = "Day High",
                value = "${tickerData.high}",
                valueColor = financialGreen,
                icon = Icons.Default.TrendingUp,
                trend = "High"
            )

            // Range Percentage
            val rangePercent = ((tickerData.high - tickerData.low) / tickerData.price * 100)
            MarketDataItem(
                title = "Range",
                value = "%.2f".format(rangePercent),
                valueColor = financialGrey,
                icon = Icons.Default.ShowChart,
                trend = "Volatility"
            )

            // Day Low
            MarketDataItem(
                title = "Day Low",
                value = "${tickerData.low}",
                valueColor = financialRed,
                icon = Icons.Default.TrendingDown,
                trend = "Low"
            )
        }

        // Range Progress Bar
        Spacer(modifier = Modifier.height(8.dp))
        //RangeProgressBar(tickerData)
    }
}


@Composable
fun VolumeTradesSection(tickerData: TickerData) {
    Column {
        Text(
            text = "Trading Activity",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MarketDataItem(
                title = "Volume",
                value = formatLargeNumber(tickerData.volume),
                valueColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.SwapVert,
                trend = "Liquidity"
            )

            MarketDataItem(
                title = "Trades",
                value = formatLargeNumber(tickerData.trades),
                valueColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.Repeat,
                trend = "Activity"
            )

            MarketDataItem(
                title = "Avg. Trade",
                value = formatLargeNumber((tickerData.volume / max(tickerData.trades, 1)).toLong()),
                valueColor = financialGrey,
                icon = Icons.Default.Calculate,
                trend = "Size"
            )
        }
    }
}

@Composable
fun AdditionalMarketData(tickerData: TickerData) {
    Column {
        Text(
            text = "Market Details",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Market and Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoChip(
                label = tickerData.market,
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                textColor = MaterialTheme.colorScheme.primary
            )

            InfoChip(
                label = tickerData.st,
                backgroundColor = when (tickerData.st.uppercase()) {
                    "OPEN" -> financialGreen.copy(alpha = 0.1f)
                    "CLOSED" -> financialRed.copy(alpha = 0.1f)
                    "HALTED" -> financialWarning.copy(alpha = 0.1f)
                    else -> financialGrey.copy(alpha = 0.1f)
                },
                textColor = when (tickerData.st.uppercase()) {
                    "OPEN" -> financialGreen
                    "CLOSED" -> financialRed
                    "HALTED" -> financialWarning
                    else -> financialGrey
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Additional metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AdditionalMetricItem(
                title = "Value Traded",
                value = "${formatLargeNumber(tickerData.value.toLong())}"
            )

            AdditionalMetricItem(
                title = "Last Update",
                value = formatRelativeTime(tickerData.timestamp)
            )
        }
    }
}

@Composable
fun MarketDataItem(
    title: String,
    value: String,
    valueColor: Color,
    icon: ImageVector,
    trend: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Icon and value row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = trend,
                tint = valueColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Trend indicator
        Text(
            text = trend,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoChip(label: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun AdditionalMetricItem(title: String, value: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Add these utility functions
private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "Just now" // Less than 1 minute
        diff < 3600000 -> "${diff / 60000}m ago" // Less than 1 hour
        diff < 86400000 -> "${diff / 3600000}h ago" // Less than 1 day
        else -> "${diff / 86400000}d ago" // More than 1 day
    }
}

private fun max(a: Long, b: Long): Long = if (a > b) a else b
@Composable
fun FinancialsTab(fundamentalData: FundamentalData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { FinancialStatsCard(fundamentalData) }
        item { ValuationMetricsCard(fundamentalData) }
        item { PerformanceMetricsCard(fundamentalData) }
    }
}

@Composable
fun FinancialStatsCard(fundamentalData: FundamentalData) {
}

@Composable
fun DividendsTab(dividendData: List<DividendData>, symbol: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dividend History - $symbol",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (dividendData.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No dividend history available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(dividendData) { dividend ->
                DividendItem(dividend)
            }
        }
    }
}

@Composable
fun FundamentalOverviewCard(fundamentalData: FundamentalData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Fundamental Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FundamentalMetricItem(
                    title = "P/E Ratio",
                    value = "%.6f".format(fundamentalData.peRatio),
                    color = if (fundamentalData.peRatio < 25) financialGreen else financialRed
                )
                FundamentalMetricItem(
                    title = "Div Yield",
                    value = "%.2f".format(fundamentalData.dividendYield) ,
                    color = if (fundamentalData.dividendYield > 2) financialGreen else financialRed
                )
                FundamentalMetricItem(
                    title = "Year Change",
                    value = "%.2f".format(fundamentalData.yearChange) ,
                    color = if (fundamentalData.yearChange >= 0) financialGreen else financialRed
                )
            }
        }
    }
}

@Composable
fun ValuationMetricsCard(fundamentalData: FundamentalData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Valuation Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValuationMetricRow(
                title = "Market Capitalization",
                value = fundamentalData.marketCap
            )
            ValuationMetricRow(
                title = "Price-to-Earnings (P/E)",
                value = fundamentalData.peRatio.toString()
            )
            ValuationMetricRow(
                title = "Free Float",
                value = fundamentalData.freeFloat
            )
            ValuationMetricRow(
                title = "Average Volume (30D)",
                value = formatLargeNumber(fundamentalData.volume30Avg.toLong())
            )
        }
    }
}

@Composable
fun PerformanceMetricsCard(fundamentalData: FundamentalData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            PerformanceMetricRow(
                title = "Yearly Change",
                value = "%.2f".format(fundamentalData.yearChange) ,
                isPositive = fundamentalData.yearChange >= 0
            )
            PerformanceMetricRow(
                title = "Dividend Yield",
                value = "${fundamentalData.dividendYield}%",
                isPositive = fundamentalData.dividendYield > 0
            )
            PerformanceMetricRow(
                title = "Sharia Compliant",
                value = if (fundamentalData.isNonCompliant) "No" else "Yes",
                isPositive = !fundamentalData.isNonCompliant
            )
        }
    }
}

@Composable
fun DividendItem(dividend: DividendData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dividend - ${dividend.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Ex-date: ${dividend.exDate} • Payment: ${dividend.paymentDate}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${dividend.amount}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = financialGreen
                )
                Text(
                    text = "Per Share",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FundamentalMetricItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ValuationMetricRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PerformanceMetricRow(title: String, value: String, isPositive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isPositive) financialGreen else financialRed
        )
    }
}


fun formatLargeNumber(value: Long): String {
    if (value < 1_000) return value.toString()
    if (value < 1_000_000) return String.format("%.1fK", value / 1_000.0)
    if (value < 1_000_000_000) return String.format("%.1fM", value / 1_000_000.0)
    if (value < 1_000_000_000_000) return String.format("%.1fB", value / 1_000_000_000.0)
    return String.format("%.1fT", value / 1_000_000_000_000.0)
}

// Update your existing CompanyDetail function to use the new structure
@Composable
fun CompanyDetail(company: CompaniesData) {
    CompanyTab(company)
}