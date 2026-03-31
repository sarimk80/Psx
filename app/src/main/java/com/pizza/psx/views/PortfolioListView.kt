package com.pizza.psx.views

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.psx.domain.model.PortfolioWithTransactions
import com.pizza.psx.domain.model.Transaction
import com.pizza.psx.presentation.helpers.formatDate
import com.pizza.psx.presentation.helpers.number_format
import com.pizza.psx.presentation.viewModel.PortfolioListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioListView(
    onBackClick: () -> Unit,
    symbol: String,
    viewModel: PortfolioListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingPortfolioState()
                uiState.error != null -> ErrorPortfolioState(
                    message = uiState.error!!,
                    onRetry = { /* viewModel.loadData(symbol) */ } // Assuming a retry function exists
                )
                uiState.listOfStocks.isNullOrEmpty() -> EmptyState()
                else -> {
                    val portfolio = uiState.listOfStocks!!.first()
                    TransactionList(portfolio = portfolio)
                }
            }
        }
    }
}

@Composable
private fun TransactionList(portfolio: PortfolioWithTransactions) {
    val totalVolume = portfolio.transactions.sumOf { tx ->
        val vol = tx.volume ?: 0

        when (tx.transactionStatus) {
            "Buy" -> vol
            "Sell" -> -vol
            else -> portfolio.portfolio.volume
        }
    }
    val totalInvestment = portfolio.transactions.sumOf { tx ->
        val price = tx.price ?: 0.0
        val volume = tx.volume ?: 0

        when(tx.transactionStatus){
            "Buy" -> price * volume
            "Sell" -> 0.0
            else -> portfolio.portfolio.volume.toDouble()
        }

//        if (tx.transactionStatus == "Buy") {
//            price * volume
//        } else {
//            0.0
//        }
    }
    val avgPrice = if (totalVolume > 0) totalInvestment / totalVolume else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item{
            Spacer(modifier = Modifier.padding(start = 8.dp))
        }
        item {
            SummaryCard(
                totalVolume = totalVolume,
                totalInvestment = totalInvestment,
                avgPrice = avgPrice
            )
        }

        item {
            SectionHeader(title = "Transaction History")
        }

        items(
            items = portfolio.transactions,
            key = { it.id ?: it.hashCode() } // Assuming Transaction has a unique id
        ) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
private fun SummaryCard(
    totalVolume: Int,
    totalInvestment: Double,
    avgPrice: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Portfolio Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Metrics in a grid-like layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "Total Volume",
                    value = totalVolume.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Avg. Price",
                    value = number_format(avgPrice),
                    modifier = Modifier.weight(1f)
                )
            }

            MetricItem(
                label = "Total Investment",
                value = number_format(totalInvestment),
                modifier = Modifier.fillMaxWidth(),
                valueStyle = MaterialTheme.typography.headlineSmall,
                isCurrency = true
            )
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge,
    isCurrency: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (isCurrency) "$value" else value,
            style = valueStyle,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    // Determine if buy or sell based on volume sign (assuming positive = buy, negative = sell)
    val isBuy = (transaction.transactionStatus ==  "Buy")
    val volume = transaction.volume ?: 0
    val price = transaction.price ?: 0.0
    val total = volume * price

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: icon + volume and price
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isBuy) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        imageVector = if (isBuy) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = if (isBuy) "Buy" else "Sell",
                        modifier = Modifier.padding(8.dp),
                        tint = if (isBuy) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isBuy) "Bought" else "Sold",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isBuy) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${kotlin.math.abs(volume)} shares @ ${"%.2f".format(price)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Right side: date and total
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rs ${number_format(total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingPortfolioState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator()
    }
}

@Composable
private fun ErrorPortfolioState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "When you buy or sell shares, they will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}