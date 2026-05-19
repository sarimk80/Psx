package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.financialRed
import com.pizza.compose.financialGreen
import com.pizza.compose.financialGrey
import com.pizza.compose.financialWarning
import com.pizza.psx.R
import com.pizza.psx.domain.model.CircuitBreakerModel
import com.pizza.psx.domain.model.TickerBreaker
import com.pizza.psx.presentation.viewModel.CircuitBreakerViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircuitBreaker(
    onBackClick: () -> Unit,
    onClick: (String) -> Unit
) {
    val viewModel: CircuitBreakerViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) { viewModel.getAllCircuitBreaker() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Circuit Breaker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.breaker == null -> BreakerLoadingState()
                uiState.error != null -> BreakerErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.getAllCircuitBreaker() }
                )
                uiState.breaker != null -> SuccessState(breaker = uiState.breaker!!, onClick = onClick)
            }
        }
    }
}

// ── Loading ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BreakerLoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ContainedLoadingIndicator()
        Text("Fetching circuit breakers…", fontSize = 13.sp)
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────
@Composable
fun BreakerErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = financialRed, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(12.dp))
        Text("Something went wrong",  fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(6.dp))
        Text(message,  fontSize = 13.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = financialGreen)
        ) {
            Text("Retry", fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Success list ──────────────────────────────────────────────────────────────
@Composable
fun SuccessState(breaker: CircuitBreakerModel,onClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (breaker.upper.isNotEmpty()) {
            item { SectionHeader("Upper Circuit", breaker.upper.size, isGain = true) }
            items(breaker.upper.size) { i ->
                AnimatedStockCard(stock = breaker.upper[i], isPositive = true, onClick = onClick)
            }
            item { Spacer(Modifier.height(8.dp)) }
        }

        if (breaker.lower.isNotEmpty()) {
            item { SectionHeader("Lower Circuit", breaker.lower.size, isGain = false) }
            items(breaker.lower.size) { i ->
                AnimatedStockCard(stock = breaker.lower[i], isPositive = false, onClick = onClick)
            }
        }

        item { Spacer(Modifier.height(60.dp)) }
    }
}

// ── Section header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(label: String, count: Int, isGain: Boolean) {
    val accent = if (isGain) financialGreen else financialRed
    val bg     = if (isGain) financialGreen.copy(alpha = 0.12f)  else financialRed.copy(alpha = 0.12f)
    val icon   = if (isGain) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(bg)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text("$count stocks", color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Animated card wrapper ─────────────────────────────────────────────────────
@Composable
private fun AnimatedStockCard(stock: TickerBreaker, isPositive: Boolean,onClick: (String) -> Unit) {
        StockCard(stock = stock, isPositive = isPositive,onClick = onClick)

}

// ── Stock card ────────────────────────────────────────────────────────────────
@Composable
fun StockCard(stock: TickerBreaker, isPositive: Boolean,onClick: (String) -> Unit) {
    val accent     = if (isPositive) financialGreen else financialRed
    val accentSoft = if (isPositive) financialGreen.copy(alpha = 0.12f)  else financialRed.copy(alpha = 0.12f)
    val changeSign = if (stock.change >= 0) "+" else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onClick(stock.symbol) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Row 1: Symbol + LDCP  |  Price + Change badge ──────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stock.symbol,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Close  ${stock.ldcp}",
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "PKR ${stock.current}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentSoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "$changeSign${stock.change}  ($changeSign${"%.2f".format(stock.change_percent)}%)",
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.8.dp)
            Spacer(Modifier.height(12.dp))

            // ── Row 2: Open | High | Low | Volume ─────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCell(label = "Open",   value = "${stock.open}", valueColor = financialGrey)
                StatCell(label = "High",   value = "${stock.high}",   valueColor = financialGreen)
                StatCell(label = "Low",    value = "${stock.low}",    valueColor = financialRed)
                StatCell(label = "Volume", value = formatVolume(stock.volume), valueColor = financialWarning)
            }
        }
    }
}

// ── Stat cell ─────────────────────────────────────────────────────────────────
@Composable
private fun StatCell(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label,  fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(3.dp))
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ── Volume formatter ──────────────────────────────────────────────────────────
private fun formatVolume(volume: Int): String = when {
    volume >= 1_000_000 -> "${"%.1f".format(volume / 1_000_000.0)}M"
    volume >= 1_000     -> "${"%.1f".format(volume / 1_000.0)}K"
    else                -> volume.toString()
}