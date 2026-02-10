package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizza.psx.domain.model.PortfolioModel
import com.pizza.psx.presentation.viewModel.PortfolioViewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.presentation.helpers.generateColorFromSymbol
import com.pizza.psx.presentation.helpers.generateColors
import com.pizza.psx.presentation.helpers.getColorFromIndex
import com.pizza.psx.presentation.helpers.getRandomColor
import com.pizza.psx.presentation.helpers.number_format
import com.pizza.psx.presentation.helpers.randomColor
import com.pizza.psx.presentation.viewModel.SearchUiState
import com.pizza.psx.presentation.viewModel.SearchViewModel
import com.pizza.psx.views.charts.ChartData
import com.pizza.psx.views.charts.DonutChartWithLegend
import com.pizza.psx.views.charts.InteractiveDonutChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.delay

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioView(
    onTickerClick: (String, String) -> Unit = { _, _ -> },
    viewModel: PortfolioViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val searchUiState by searchViewModel.uiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }

    var selectedSymbol by remember { mutableStateOf("") }
    var stockCount by remember { mutableStateOf("1") }

    val portfolioItems by viewModel.portfolioModels.collectAsStateWithLifecycle()


//    LaunchedEffect(Unit) {
//        //searchViewModel.getSymbolList()
//        while (true){
//            viewModel.getAllPortfolioTicker()
//            delay(70_000)
//        }
//    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Portfolio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold)
                        },

            )
        },
        floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(bottom = 50.dp)
                        .navigationBarsPadding()
                        .navigationBarsPadding()
                        .navigationBarsPadding()
                        .navigationBarsPadding(),
                    onClick = {
                        // Open bottom sheet
                        showBottomSheet = true
                    },
                ) {
                    Icon(Icons.Default.Add, "Add stock")
                }

        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if(showBottomSheet){
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(),
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    AddStockBottomSheetContent(
                        onTickerClick = {type, symbol ->
                            if(viewModel.checkIfSymbolExist(symbol = symbol)){
                                viewModel.addToPortfolioModel(symbol, volume = 0)
                                showBottomSheet = false
                            }else{
                                selectedSymbol = symbol
                                showAddStockDialog = true
                            }

                        },
                        onDismiss = { showBottomSheet = false },
                        searchUiState = searchUiState,
                        listOfPortfolio = portfolioItems
                    )

                }
            }
            if(showAddStockDialog){
                AddStockDialog(
                    symbol = selectedSymbol,
                    stockCount = stockCount,
                    errorMessage = "",
                    onDismiss = { showAddStockDialog = false },
                    onConfirm = {
                        viewModel.addToPortfolioModel(selectedSymbol, volume = stockCount.toInt())
                        showAddStockDialog = false
                        showBottomSheet = false

                                },
                    onStockCountChange = { stockCount = it }
                )
            }
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

@Composable
fun AddStockDialog(
    symbol: String,
    stockCount: String,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onStockCountChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add $symbol to Portfolio",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = stockCount,
                    onValueChange = onStockCountChange,
                    label = { Text("Number of Stocks") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirm() }
                    ),
                    singleLine = true,
                    isError = errorMessage != null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Numbers,
                            contentDescription = null
                        )
                    }
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Quick selection buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("1", "10", "50", "100").forEach { quantity ->
                        FilterChip(
                            selected = stockCount == quantity,
                            onClick = { onStockCountChange(quantity) },
                            label = { Text(quantity) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = stockCount.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    )
}

@Composable
fun AddStockBottomSheetContent(
    searchUiState: SearchUiState,
    onTickerClick: (String, String) -> Unit,
    onDismiss: () -> Unit,
    listOfPortfolio: List<PortfolioModel>
) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        // Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Add Stock",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search Field
        OutlinedTextField(
            value = query,
            onValueChange = { query = it.uppercase() },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            label = { Text("Search symbol") },
            placeholder = { Text("DCR, EFERT, HUBC...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Content States
        when {
            searchUiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            searchUiState.error != null -> {
                Text(
                    text = searchUiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            searchUiState.symbolList != null -> {
                PortfolioSymbolList(
                    symbols = searchUiState.symbolList.data,
                    searchQuery = query,
                    modifier = Modifier.weight(1f),
                    onTickerClick = onTickerClick,
                    listOfPortfolio = listOfPortfolio
                )
            }
        }
    }
}

@Composable
fun PortfolioSymbolList(
    symbols: List<String>,
    searchQuery: String,
    modifier: Modifier = Modifier,
    onTickerClick: (String, String) -> Unit,
    listOfPortfolio: List<PortfolioModel>
) {
    val filtered = remember(symbols, searchQuery) {
        if (searchQuery.isBlank()) symbols
        else symbols.filter {
            it.contains(searchQuery, ignoreCase = true) ||
                    it.contains(searchQuery, ignoreCase = true)
        }
    }

    if (filtered.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No results found")
        }
    } else {
        LazyColumn(modifier) {
            items(filtered) { item ->
                SymbolRow(
                    symbol = item,
                    onClick = { onTickerClick(item, item) },
                    listOfPortfolio = listOfPortfolio
                )
            }
        }
    }
}

@Composable
fun SymbolRow(
    symbol: String,
    onClick: () -> Unit,
    listOfPortfolio:  List<PortfolioModel>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol.take(3).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,

            )
        }
        Spacer(modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

        }
        PortfolioActionButton(symbol = symbol, portfolioItems = listOfPortfolio,
            onAdd = onClick )
    }
}

@Composable
fun PortfolioActionButton(
    symbol: String,
    portfolioItems: List<PortfolioModel>,
    onAdd: () -> Unit,

    modifier: Modifier = Modifier
) {
    val isInPortfolio = portfolioItems.any { it.symbol == symbol }

    Button(
        onClick = { onAdd() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isInPortfolio) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = if (isInPortfolio) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    ) {
        Icon(
            imageVector = if (isInPortfolio) Icons.Default.Check else Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isInPortfolio) "Added" else "Add",
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
fun EmptyWatchlistState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = "Empty watchlist",
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No stocks in watchlist",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Add your favorite stocks to track them here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )

        }
    }
}

@Composable
fun PortfolioLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                "Loading watchlist...",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Updated WatchlistItemCard with more actions
@Composable
fun CompactWatchlistItemCard(
    item: Ticker,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onTickerClick: () -> Unit,
    isHideVolume: Boolean = true,
    isHideSector: Boolean = true
) {
    Card(
        onClick = onTickerClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.data.changePercent >= 0) financialGreen.copy(alpha = 0.1f) else financialRed.copy(
                                alpha = 0.1f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.data.symbol.take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.data.changePercent >= 0) financialGreen else financialRed
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.data.symbol,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    when {
                        isHideVolume -> {
                            Text(
                                text = "${item.data.stockCount} x ${number_format(item.data.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        isHideSector -> {
                            Text(
                                text = item.data.sectorName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = number_format(item.data.price * item.data.stockCount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(bottom = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "${if (item.data.change >= 0) "+" else ""}${String.format("%.2f", item.data.change)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (item.data.changePercent >= 0) financialGreen else financialRed
                    )
                    Spacer(modifier = Modifier.padding(end = 4.dp))
                    Box(
                        modifier = Modifier
                            //.padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.data.changePercent >= 0)
                                    financialGreen.copy(alpha = 0.1f)
                                else
                                    financialRed.copy(alpha = 0.1f)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp), // size based on text
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${if (item.data.change >= 0) "+" else ""}${String.format("%.2f", item.data.changePercent)}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (item.data.changePercent >= 0) financialGreen else financialRed
                        )
                    }

                }

            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

// Helper functions for formatting


private fun formatCurrency(value: Double): String {
    return when {
        value >= 1_000_000 -> "$${"%.2fM".format(value / 1_000_000.0)}"
        value >= 1_000 -> "$${"%.1fK".format(value / 1_000.0)}"
        else -> "$${String.format("%.2f", value)}"
    }
}

private fun formatNumber(number: Long): String {
    return String.format("%,d", number)
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp * 1000) // Assuming timestamp is in seconds
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun PortfolioContent(
    items: List<Ticker>,
    onRemoveItem: (String) -> Unit,
    onEditItem: (Ticker) -> Unit,
    onTickerClick: (String, String) -> Unit
) {

    val chartSampleData = remember(items) {

        items.mapIndexed { index,ticker ->
            ChartData(
                label = ticker.data.symbol,
                value = ticker.data.stockCount.toFloat(),
                color = getColorFromIndex(index),
                price = (ticker.data.price.toFloat() * ticker.data.stockCount.toFloat())
            )
        }
    }
    if(items.isEmpty()){
        EmptyWatchlistState(
            onAddClick = {}
        )
    }
    else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                DonutChartWithLegend(
                    data = chartSampleData,

                    )
            }

            item {
                Text(
                    text = "${items.size} stocks",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(items) { item ->
                CompactWatchlistItemCard(
                    item = item,
                    onRemove = { onRemoveItem(item.data.symbol) },
                    onTickerClick = { onTickerClick("REG", item.data.symbol) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}