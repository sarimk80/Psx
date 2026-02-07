package com.pizza.psx.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.financialGreen
import com.pizza.compose.financialGrey
import com.pizza.compose.financialRed
import com.pizza.psx.domain.model.SectorResponse
import com.pizza.psx.domain.model.StockData
import com.pizza.psx.domain.model.TopStocks
import com.pizza.psx.presentation.helpers.number_format
import com.pizza.psx.presentation.viewModel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotStocks(
    onTickerClick: (String, String) -> Unit = { _, _ -> }
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getGainersAndLosers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Market Movers",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },

            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.getGainersAndLosers() }
                )
                uiState.stocks != null -> MarketMoversContent(
                    stocks = uiState.stocks!!,
                    gainers = uiState.gainers ?: emptyList(),
                    losers = uiState.losers ?: emptyList(),
                    active = uiState.active ?: emptyList(),
                    onTickerClick = onTickerClick
                )
                else -> LoadingState()
            }
        }
    }
}

@Composable
fun MarketMoversContent(
    stocks: SectorResponse,
    gainers: List<StockData>,
    losers: List<StockData>,
    active: List<StockData>,
    onTickerClick: (String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Top Gainers", "Top Losers", "Most Active")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp) // Add horizontal padding to the entire column
    ) {
        // Header section with better spacing
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 10.dp, start = 16.dp, end = 16.dp) // Separate header from tabs
        ) {
            Text(
                text = "Hot Stocks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Top performing stocks in the market",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {

            tabs.forEachIndexed { index, title ->
                SegmentedButton(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = tabs.size
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surface,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedTab == index)
                            FontWeight.SemiBold
                        else
                            FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content
        when (selectedTab) {
            0 -> StockList(
                stocks = gainers,
                isGainer = true,
                title = "Top Gainers",
                onTickerClick = { _,_ -> }
            )
            1 -> StockList(
                stocks = losers,
                isGainer = false,
                title = "Top Losers",
                onTickerClick = { _,_ -> }
            )
            2 -> ActiveStocksList(
                stocks = active,
                title = "Most Active",
                onTickerClick = { _,_ -> }
            )
        }
    }
}

@Composable
fun MarketOverviewCards(
    gainersCount: Int,
    losersCount: Int,
    activeCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gainers Card
        StatCard(
            title = "Gainers",
            count = gainersCount,
            icon = Icons.Filled.TrendingUp,
            iconColor = financialGreen,
            backgroundColor = financialGreen.copy(alpha = 0.08f),
            gradientColors = listOf(
                financialGreen.copy(alpha = 0.15f),
                financialGreen.copy(alpha = 0.05f)
            ),
            modifier = Modifier.weight(1f) // Pass weight here
        )

        // Losers Card
        StatCard(
            title = "Losers",
            count = losersCount,
            icon = Icons.Filled.TrendingDown,
            iconColor = financialRed,
            backgroundColor = financialRed.copy(alpha = 0.08f),
            gradientColors = listOf(
                financialRed.copy(alpha = 0.15f),
                financialRed.copy(alpha = 0.05f)
            ),
            modifier = Modifier.weight(1f) // Pass weight here
        )

        // Active Card
        StatCard(
            title = "Active",
            count = activeCount,
            icon = Icons.Filled.BarChart,
            iconColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            gradientColors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
            modifier = Modifier.weight(1f) // Pass weight here
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    gradientColors: List<Color>? = null,

) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = iconColor.copy(alpha = 0.3f),
                ambientColor = iconColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = iconColor.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (gradientColors != null && gradientColors.size >= 2) {
                        Brush.verticalGradient(
                            colors = gradientColors,
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    } else {
                        SolidColor(backgroundColor)
                    }
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon and Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = iconColor
                    )
                }

                // Count with animation
                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    },
                    label = "Count animation"
                ) { targetCount ->
                    Text(
                        text = targetCount.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Trend indicator (optional)
                if (title == "Gainers" || title == "Losers") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (title == "Gainers") Icons.Filled.ArrowUpward
                            else Icons.Filled.ArrowDownward,
                            contentDescription = "Trend",
                            tint = iconColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )

                        Text(
                            text = if (title == "Gainers") "Rising" else "Falling",
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timelapse,
                            contentDescription = "Active",
                            tint = iconColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )

                        Text(
                            text = "Trading",
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StockList(
    stocks: List<StockData>,
    isGainer: Boolean,
    title: String,
    onTickerClick: (String, String) -> Unit,
) {
    if (stocks.isEmpty()) {
        EmptyState(message = "No $title available")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(stocks.take(20)) { index, stock ->
                CompactStockItem(
                    stock = stock,
                    isGainer = isGainer,
                    rank = index + 1,
                    onTickerClick = onTickerClick
                )
            }
        }
    }
}

@Composable
fun ActiveStocksList(
    stocks: List<StockData>,
    title: String,
    onTickerClick: (String, String) -> Unit,
) {
    if (stocks.isEmpty()) {
        EmptyState(message = "No active stocks available")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(stocks.take(20)) { index, stock ->
                ActiveStockItem(
                    stock = stock,
                    rank = index + 1,
                    onTickerClick = onTickerClick
                )
            }
        }
    }
}

@Composable
fun StockItem(
    stock: StockData,
    isGainer: Boolean,
    rank: Int,
    onTickerClick: (String, String) -> Unit,
) {
    val priceColor = if (isGainer) financialGreen else financialRed
    val changeSign = if (isGainer && stock.change.toDoubleOrNull() ?: 0.0 > 0) "+" else ""
    val changePercentage = calculateChangePercentage(stock.current, stock.ldcp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onTickerClick("REG", stock.script_name) },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Section: Rank, Symbol/Name, and Price with Change
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Left Side: Rank and Stock Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Rank Badge - More prominent
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = if (isGainer) {
                                        listOf(
                                            financialGreen.copy(alpha = 0.15f),
                                            financialGreen.copy(alpha = 0.08f)
                                        )
                                    } else {
                                        listOf(
                                            financialRed.copy(alpha = 0.15f),
                                            financialRed.copy(alpha = 0.08f)
                                        )
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rank.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = priceColor
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Symbol and Volume
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = stock.script_name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 140.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Volume",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = formatVolume(stock.volume),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Right Side: Price and Change - More prominent
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = number_format(stock.current.toDouble()),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Change Badge with background
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = priceColor.copy(alpha = 0.12f),
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isGainer) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = if (isGainer) "Increase" else "Decrease",
                                tint = priceColor,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                text = "$changeSign${stock.change}",
                                style = MaterialTheme.typography.labelLarge,
                                color = priceColor,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "($changeSign$changePercentage%)",
                                style = MaterialTheme.typography.labelMedium,
                                color = priceColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Divider for better separation
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Bottom Section: Price Statistics - Improved layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriceStatItem(
                    label = "High",
                    value = stock.high,
                    color = financialGreen,
                    icon = Icons.Default.TrendingUp
                )

                PriceStatItem(
                    label = "Low",
                    value = stock.low,
                    color = financialRed,
                    icon = Icons.Default.TrendingDown
                )

                PriceStatItem(
                    label = "Open",
                    value = stock.open,
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Circle
                )

                PriceStatItem(
                    label = "Prev",
                    value = stock.ldcp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    icon = Icons.Default.History
                )
            }
        }
    }
}

@Composable
fun PriceStatItem(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        // Icon with subtle background
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // Value - More prominent
        Text(
            text = number_format(value.toDouble()),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun CompactStockItem(
    stock: StockData,
    isGainer: Boolean,
    rank: Int,
    onTickerClick: (String, String) -> Unit,
) {
    val priceColor = if (isGainer) financialGreen else financialRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onTickerClick("REG", stock.script_name) },
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Rank and Symbol
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (isGainer) financialGreen.copy(alpha = 0.1f)
                            else financialRed.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = priceColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Symbol
                Column {
                    Text(
                        text = stock.script_name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 100.dp)
                    )

                    // Price range
                    Text(
                        text = "${stock.low} - ${stock.high}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right: Price Info
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Current Price
                Text(
                    text = stock.current,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Change
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stock.change,
                        style = MaterialTheme.typography.bodyMedium,
                        color = priceColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Open indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "O:${stock.open}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveStockItem(
    stock: StockData,
    rank: Int,
    onTickerClick: (String, String) -> Unit,
) {
    val changeValue = stock.change.toDouble()
    val isGainer = changeValue > 0
    val priceColor = if (changeValue > 0) financialGreen
    else if (changeValue < 0) financialRed
    else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onTickerClick("REG", stock.script_name) },
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Rank and Symbol
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShowChart,
                        contentDescription = "Active",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Symbol and Name
                Column {
                    Text(
                        text = stock.script_name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 120.dp)
                    )

                    Row {
                        Text(
                            text = "Vol: ${formatVolume(stock.volume)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Right side: Price and Info
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stock.current,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isGainer) Icons.Default.ArrowUpward
                        else Icons.Default.ArrowDownward,
                        contentDescription = "Change",
                        tint = priceColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${if (changeValue > 0) "+" else ""}${stock.change}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = priceColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                color = if (isGainer) financialGreen.copy(alpha = 0.1f)
                                else financialRed.copy(alpha = 0.1f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stock.trend.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = priceColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.BarChart,
                contentDescription = "Empty",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ContainedLoadingIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading market data...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Failed to load market data",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Retry", fontWeight = FontWeight.Medium)
            }
        }
    }
}

// Helper functions
private fun calculateChangePercentage(current: String, previous: String): String {
    return try {
        val currentVal = current.toDoubleOrNull() ?: 0.0
        val previousVal = previous.toDoubleOrNull() ?: 0.0
        if (previousVal == 0.0) "0.00" else "%.2f".format(((currentVal - previousVal) / previousVal) * 100)
    } catch (e: Exception) {
        "0.00"
    }
}

private fun formatVolume(volume: String): String {
    return try {
        val vol = volume.replace(",", "").toLongOrNull() ?: 0
        when {
            vol >= 1_000_000_000 -> "%.1fB".format(vol / 1_000_000_000.0)
            vol >= 1_000_000 -> "%.1fM".format(vol / 1_000_000.0)
            vol >= 1_000 -> "%.1fK".format(vol / 1_000.0)
            else -> vol.toString()
        }
    } catch (e: Exception) {
        volume
    }
}

// Add this extension function for tab indicator
fun Modifier.tabIndicatorOffset(
    currentTabPosition: androidx.compose.material3.TabPosition
): Modifier = composed(
    inspectorInfo = {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth = currentTabPosition.width
    val indicatorOffset = currentTabPosition.left
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}