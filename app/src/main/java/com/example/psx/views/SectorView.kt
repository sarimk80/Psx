@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.psx.views

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.financialGreen
import com.example.compose.financialRed
import com.example.psx.domain.model.Datum
import com.example.psx.domain.model.Sector
import com.example.psx.presentation.viewModel.SectorViewModel

@Composable
fun SectorView() {
    val viewModel: SectorViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getSectorAll()
    }

    Scaffold(
        topBar = {
            SectorTopAppBar(
                onRefresh = { viewModel.getSectorAll() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> SectorLoadingState()
                uiState.error != null -> SectorErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.getSectorAll() }
                )
                uiState.stocks != null -> SectorContent(uiState.stocks!!)
                else -> SectorLoadingState()
            }
        }
    }
}

@Composable
fun SectorTopAppBar(onRefresh: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = "Sectors",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Column {
                    Text(
                        text = "Market Sectors",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Sector-wise performance",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh sectors"
                )
            }
        }
    )
}

@Composable
fun SectorContent(sector: Sector) {
    val sortedSectors = sector.data.entries.sortedByDescending { it.value.avgChangePercent }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Market Summary Card
        item {
            SectorMarketSummary(sector.data)
        }

        // Sector Performance Header
        item {
            Text(
                text = "Sector Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Sector List
        items(sortedSectors, key = { it.key }) { (sectorName, data) ->
            SectorItem(
                sectorName = sectorName,
                data = data,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectorMarketSummary(sectorData: Map<String, Datum>) {
    val totalGainers = sectorData.values.sumOf { it.gainers }
    val totalLosers = sectorData.values.sumOf { it.losers }
    val totalVolume = sectorData.values.sumOf { it.totalVolume }
    val totalValue = sectorData.values.sumOf { it.totalValue }
    val avgPerformance = sectorData.values.map { it.avgChangePercent }.average()

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
            Text(
                text = "Market Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Performance Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectorMetricItem(
                    title = "Overall Performance",
                    value = "${avgPerformance.format(2)}%",
                    color = if (avgPerformance >= 0) financialGreen else financialRed,
                    icon = if (avgPerformance >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown
                )

                SectorMetricItem(
                    title = "Total Sectors",
                    value = sectorData.size.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Business
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectorStatItem(
                    title = "Gainers",
                    value = totalGainers.toString(),
                    color = financialGreen
                )

                SectorStatItem(
                    title = "Losers",
                    value = totalLosers.toString(),
                    color = financialRed
                )

                SectorStatItem(
                    title = "Total Volume",
                    value = formatLargeNumber(totalVolume),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SectorItem(
    sectorName: String,
    data: Datum,
    modifier: Modifier = Modifier
) {
    val isPositive = data.avgChangePercent >= 0
    val performanceColor = if (isPositive) financialGreen else financialRed
    val containerColor = if (isPositive) {
        financialGreen.copy(alpha = 0.1f)
    } else {
        financialRed.copy(alpha = 0.1f)
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = {
            // Handle sector click - could navigate to sector detail
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sector Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sectorName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Sector Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectorMiniStat(
                        title = "Stocks",
                        value = data.symbols.size.toString()
                    )
                    SectorMiniStat(
                        title = "Gainers",
                        value = data.gainers.toString(),
                        color = financialGreen
                    )
                    SectorMiniStat(
                        title = "Losers",
                        value = data.losers.toString(),
                        color = financialRed
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Performance Indicator
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Performance Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(containerColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = if (isPositive) "Positive" else "Negative",
                            tint = performanceColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${data.avgChangePercent.format(2)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = performanceColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Avg Change",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectorMetricItem(
    title: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectorStatItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectorMiniStat(
    title: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectorLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContainedLoadingIndicator()
            Text(
                text = "Loading sector data...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectorErrorState(error: String, onRetry: () -> Unit) {
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

// Extension functions for formatting
private fun Double.format(digits: Int): String = "%.${digits}f".format(this)

private fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000_000 -> "${(number / 1_000_000_000.0).format(1)}B"
        number >= 1_000_000 -> "${(number / 1_000_000.0).format(1)}M"
        number >= 1_000 -> "${(number / 1_000.0).format(1)}K"
        else -> number.toString()
    }
}