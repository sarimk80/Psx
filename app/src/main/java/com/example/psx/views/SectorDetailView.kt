package com.example.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.TableInfo
import com.example.psx.domain.model.Datum
import com.example.psx.presentation.viewModel.PortfolioViewModel
import com.example.psx.presentation.helpers.formatVolume
import com.example.psx.presentation.helpers.formatCurrency


@Composable
fun  SectorDetailView(sectorName: String,
                      sector: Datum,
                      onTickerClick: (String, String) -> Unit = { _, _ -> },
                      onBack: () -> Unit)
{

    val viewModel: PortfolioViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getSectorTicker(sector.symbols)
    }

    Scaffold(
        topBar = {

            SectorDetailTopBar(
                sectorName = sectorName,
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)
        ){
            // Sector Overview Card
            SectorOverviewCard(sector = sector)


            Spacer(modifier = Modifier.height(8.dp))

            when{
                uiState.isLoading -> LoadingState()
                uiState.error !=null -> Text(uiState.error!!)
                uiState.listOfStocks!=null -> PortfolioContent(
                    items = uiState.listOfStocks!!,
                    onRemoveItem = { symbol ->
                        //viewModel.removeFromWatchlist(symbol)
                    },
                    onEditItem = { item ->
                        // Open edit dialog
                    },
                    onTickerClick = onTickerClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectorDetailTopBar(
    sectorName: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = sectorName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },

        )
}


@Composable
private fun SectorOverviewCard(sector: Datum) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Performance Indicator
            PerformanceIndicator(changePercent = sector.avgChangePercent)

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Volume",
                    value = formatVolume(sector.totalVolume.toDouble()),
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    label = "Value",
                    value = formatCurrency(sector.totalValue),
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    label = "Trades",
                    value = sector.totalTrades.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Market Sentiment
            MarketSentimentIndicator(
                gainers = sector.gainers,
                losers = sector.losers,
                unchanged = sector.unchanged
            )
        }
    }
}

@Composable
private fun PerformanceIndicator(changePercent: Double) {
    val isPositive = changePercent > 0
    val isNeutral = changePercent == 0.0
    val color = when {
        isPositive -> MaterialTheme.colorScheme.primary
        isNeutral -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.error
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when {
                isPositive -> Icons.Default.TrendingUp
                isNeutral -> Icons.Default.TrendingFlat
                else -> Icons.Default.TrendingDown
            },
            contentDescription = "Performance",
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${if (isPositive) "+" else ""}${"%.2f".format(changePercent)}%",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Avg Change",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

//@Composable
//private fun StatItem_01(
//    label: String,
//    value: String,
//    color: Color
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = value,
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.SemiBold,
//            color = color
//        )
//        Text(
//            text = label,
//            style = MaterialTheme.typography.labelSmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}

@Composable
private fun MarketSentimentIndicator(
    gainers: Long,
    losers: Long,
    unchanged: Long
) {
    val total = gainers + losers + unchanged
    if (total == 0L) return

    Column {
        Text(
            text = "Market Sentiment",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val gainerWeight = maxOf(gainers.toFloat(), 0.01f)
        val loserWeight = maxOf(losers.toFloat(), 0.01f)
        val unchangedWeight = maxOf(unchanged.toFloat(), 0.01f)


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            // Gainers (Green) - only show if there are gainers
            if (gainers > 0) {
                Box(
                    modifier = Modifier
                        .weight(gainerWeight)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Unchanged (Gray) - only show if there are unchanged
            if (unchanged > 0) {
                Box(
                    modifier = Modifier
                        .weight(unchangedWeight)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.outline)
                )
            }

            // Losers (Red) - only show if there are losers
            if (losers > 0) {
                Box(
                    modifier = Modifier
                        .weight(loserWeight)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error)
                )
            }

            // If all are zero, show empty bar
            if (gainers == 0L && losers == 0L && unchanged == 0L) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SentimentBadge(
                count = gainers,
                color = MaterialTheme.colorScheme.primary,
                label = "Gainers"
            )
            SentimentBadge(
                count = unchanged,
                color = MaterialTheme.colorScheme.outline,
                label = "Unchanged"
            )
            SentimentBadge(
                count = losers,
                color = MaterialTheme.colorScheme.error,
                label = "Losers"
            )
        }
    }
}

@Composable
private fun SentimentBadge(
    count: Long,
    color: Color,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

