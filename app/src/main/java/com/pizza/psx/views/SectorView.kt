@file:OptIn(ExperimentalMaterial3Api::class)

package com.pizza.psx.views

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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.psx.domain.model.StockData
import com.pizza.psx.presentation.viewModel.SectorViewModel

// ---------------------------------------------------------------------------------------------
// Derived counts — gainers/losers computed from each stock's change value
// ---------------------------------------------------------------------------------------------

data class SectorCounts(
    val totalStocks: Int,
    val gainers: Int,
    val losers: Int
)

fun List<StockData>.toSectorCounts(): SectorCounts {
    var gainers = 0
    var losers = 0
    for (stock in this) {
        val change = stock.change.toDoubleOrNull() ?: 0.0
        when {
            change > 0 -> gainers++
            change < 0 -> losers++
        }
    }
    return SectorCounts(totalStocks = size, gainers = gainers, losers = losers)
}

data class MarketTotals(
    val totalSectors: Int,
    val totalGainers: Int,
    val totalLosers: Int
)
// ---------------------------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------------------------

@Composable
fun SectorView(
    onSectorClick: (String, List<StockData>) -> Unit = { _, _ -> }
) {
    val viewModel: SectorViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getSectorAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Market Sectors",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.getSectorAll() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
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
                uiState.allSectors != null -> {
                    val sectors = uiState.allSectors!!.sectors
                    val countsBySector = remember(sectors) {
                        sectors.mapValues { (_, stocks) -> stocks.toSectorCounts() }
                    }


                    val sortedSectors = remember(sectors, countsBySector) {
                        sectors.entries.sortedByDescending { (name, _) -> countsBySector[name]?.gainers ?: 0 }
                    }
                    val marketTotals = remember(countsBySector) {
                        MarketTotals(
                            totalSectors = countsBySector.size,
                            totalGainers = countsBySector.values.sumOf { it.gainers },
                            totalLosers = countsBySector.values.sumOf { it.losers }
                        )
                    }


                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        item {
                            MarketTotalsHeader(totals = marketTotals)
                        }

                        items(sortedSectors, key = { it.key }) { (sectorName, stocks) ->
                            val counts = stocks.toSectorCounts()
                            SectorRow(
                                sectorName = sectorName,
                                counts = counts,
                                onClick = { onSectorClick(sectorName, stocks) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
                else -> SectorLoadingState()
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Sector row — name, total stocks, gainers, losers
// ---------------------------------------------------------------------------------------------
@Composable
fun MarketTotalsHeader(totals: MarketTotals) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeaderStat(
                label = "Sectors",
                value = totals.totalSectors.toString(),
                color = MaterialTheme.colorScheme.primary
            )
            HeaderDivider()
            HeaderStat(
                label = "Active",
                value = totals.totalGainers.toString(),
                color = financialGreen
            )
            HeaderDivider()
            HeaderStat(
                label = "Losers",
                value = totals.totalLosers.toString(),
                color = financialRed
            )
        }
    }
}

@Composable
fun HeaderStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HeaderDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
@Composable
fun SectorRow(
    sectorName: String,
    counts: SectorCounts,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sectorName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${counts.totalStocks} stocks",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CountPill(label = "Gainers", value = counts.gainers, color = financialGreen)
                CountPill(label = "Losers", value = counts.losers, color = financialRed)
            }
        }
    }
}

@Composable
fun CountPill(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Loading / error states
// ---------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectorLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContainedLoadingIndicator(modifier = Modifier.size(80.dp))
            Text(
                text = "Loading sector data...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SectorErrorState(error: String, onRetry: () -> Unit) {
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
                text = "Failed to load sectors",
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