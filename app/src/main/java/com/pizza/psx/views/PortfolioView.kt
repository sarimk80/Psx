package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Money
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
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.psx.domain.model.Ticker
import com.pizza.psx.domain.model.Transaction
import com.pizza.psx.presentation.helpers.generateColorFromSymbol
import com.pizza.psx.presentation.helpers.generateColors
import com.pizza.psx.presentation.helpers.getColorFromIndex
import com.pizza.psx.presentation.helpers.getRandomColor
import com.pizza.psx.presentation.helpers.number_format
import com.pizza.psx.presentation.helpers.randomColor
import com.pizza.psx.presentation.viewModel.IndexList
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
    onTickerClick: (String, String,Double) -> Unit = { _, _ ,_-> },
    viewModel: PortfolioViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    onTickerTransactionClick:(String) -> Unit = {_ -> },
) {
    val uiState by viewModel.uiState
    val searchUiState by searchViewModel.uiState
    val indexUiState by viewModel.indexUiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showOptionSheet by remember { mutableStateOf(false) }
    var clickedTickerPrice by remember { mutableStateOf(0.0) }
    var isFromVolumeUpdate by remember { mutableStateOf(false) }

    var selectedSymbol by remember { mutableStateOf("") }
    var stockCount by remember { mutableStateOf("1") }
    var stockPrice by remember { mutableStateOf("") }
    var stockDate by remember { mutableStateOf(1705334400000) }
    var selectedStockPrice by remember { mutableStateOf<Double?>(null) }
    var stockStatus by remember { mutableStateOf("") }

    val portfolioItems by viewModel.portfolioModels.collectAsStateWithLifecycle()

    fun insertTransactionAndSymbol(_isFromVolumeUpdate: Boolean = false){
        viewModel.addToPortfolioModel(selectedSymbol,
            volume = stockCount.toInt(),
            isFromEditVolume = _isFromVolumeUpdate,
            transaction = Transaction(
                portfolioSymbol = selectedSymbol,
                volume = stockCount.toInt(),
                date = stockDate,
                price = stockPrice.toDouble(),
                transactionStatus = "Buy"
            )
        )
    }




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
                                insertTransactionAndSymbol()
                                showBottomSheet = false
                            }else{
                                viewModel.getChartIndex(symbol)
                                selectedSymbol = symbol
                                showAddStockDialog = true
                            }

                        },
                        onDismiss = { showBottomSheet = false },
                        searchUiState = searchUiState,
                        listOfPortfolio = portfolioItems,
                        onDelete = {symbol ->
                            viewModel.removeFromWatchlist(symbol)
                            showBottomSheet = false
                        }
                    )

                }
            }
            if(showAddStockDialog){


                AddStockBottomSheet(
                    symbol = selectedSymbol,
                    stockCount = stockCount,
                    price = stockPrice,
                    selectedDate = stockDate,
                    errorMessage = "",
                    onDismiss = { showAddStockDialog = false },
                    onConfirm = {
                        insertTransactionAndSymbol(true)
                        showAddStockDialog = false
                        showBottomSheet = false
                        isFromVolumeUpdate = false

                                },
                    onStockCountChange = { stockCount = it },
                    onDateChange = { stockDate = it ?: System.currentTimeMillis() },
                    onPriceChange = {stockPrice = it },
                    indexUiState = indexUiState,
                    listOfPortfolio = portfolioItems,
                    onValueChange = {symbol ->
                        viewModel.getChartIndex(symbol)
                        selectedSymbol = symbol
                    },
                    stockStatus = stockStatus,
                    onStockStatusChange = {
                        stockStatus = it
                    }


                )
            }

            if(showOptionSheet){
                ModalBottomSheet(
                    onDismissRequest = { showOptionSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    TickerOptions(
                        tickerName = selectedSymbol,
                        onHistoryClick = {
                            onTickerTransactionClick(selectedSymbol)
                        },
                        onDetailClick = {
                            onTickerClick("REG",selectedSymbol,clickedTickerPrice)
                        }
                    )
                }
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
                    onTickerClick = { type,symbol,price ->
                        showOptionSheet = true
                        selectedSymbol = symbol
                        clickedTickerPrice = price
                    },
                    onUpdateTicker = { symbol,tickerStockCount,price ->
                        viewModel.getChartIndex(symbol)
                        selectedSymbol = symbol
                        stockCount = tickerStockCount.toString()
                        stockPrice = price.toString()
                        isFromVolumeUpdate = true
                        showAddStockDialog = true
                    },
                    onAddTransactionClick = {
                        viewModel.getChartIndex(portfolioItems.first().symbol)
                        selectedSymbol = portfolioItems.first().symbol
                        showAddStockDialog = true
                    }

                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerOptions(
    tickerName: String? = null,
    onHistoryClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Header Section
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = tickerName?.let { "Explore $it" } ?: "Explore Ticker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Select what you would like to view",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Options Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            OptionCard(
                title = "Transaction History",
                subtitle = "View past trades and activity",
                icon = Icons.Default.History,
                onClick = onHistoryClick
            )

            OptionCard(
                title = "Ticker Details",
                subtitle = "View price info, market data and metrics",
                icon = Icons.Default.Info,
                onClick = onDetailClick
            )
        }
    }
}

@Composable
private fun OptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Icon container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockBottomSheet(
    symbol: String,
    stockCount: String,
    price: String,
    selectedDate: Long?, // Timestamp in milliseconds
    errorMessage: String?,
    onDismiss: () -> Unit,
    onStockCountChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDateChange: (Long?) -> Unit,
    onConfirm: () -> Unit,
    indexUiState: IndexList,
    listOfPortfolio: List<PortfolioModel>,
    onValueChange: (String) -> Unit,
    stockStatus: String,
    onStockStatusChange: (String) -> Unit

) {
    var priceText by remember { mutableStateOf(price) }
    var userHasTyped by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    val options = listOfPortfolio.map { it.symbol }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(
        symbol.ifEmpty { options[0] }
    ) }

    // Format date for display
    val formattedDate = remember(selectedDate) {
        selectedDate?.let {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        } ?: "Select Date"
    }

    // Update price from API when not typed by user
    LaunchedEffect(indexUiState.listOfStocks) {
        if (!userHasTyped) {
            val apiPrice = indexUiState.listOfStocks
                ?.data
                ?.firstOrNull()
                ?.price
                 ?.toString() ?: ""
            if (apiPrice.isNotEmpty()) {
                priceText = apiPrice
                onPriceChange(apiPrice)
            }
        }
    }

    // Bottom sheet state – fully expanded by default
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        //dragHandle = { BottomSheetDragHandle() } // optional, but recommended
    ) {
        // Content of the bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Title
            Text(
                text = "Add $symbol to Portfolio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("Buy", "Sell").forEach { status ->
                    val isSelected = stockStatus == status
                    val isBuy = status == "Buy"

                    FilterChip(
                        selected = isSelected,
                        onClick = { onStockStatusChange(status) },
                        label = {
                            Text(
                                text = status,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isBuy)
                                financialGreen.copy(alpha = 0.6f)  // green
                            else
                                financialRed.copy(alpha = 0.6f), // red

                            selectedLabelColor = Color.White,

                            containerColor = if (isBuy)
                                Color(0xFFE8F5E9)
                            else
                                Color(0xFFFFEBEE),

                            labelColor = if (isBuy)
                                financialGreen
                            else
                                financialRed
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isBuy)
                                financialGreen.copy(alpha = 0.5f)
                            else
                                financialRed.copy(alpha = 0.5f),

                            selectedBorderColor = Color.Transparent,
                            enabled = true,
                            selected = isSelected
                        ),
                        modifier = Modifier.height(40.dp).padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                TextField(
                    value = selectedOption,
                    onValueChange = onValueChange,
                    readOnly = true,
                    label = { Text("Select symbol") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption = option
                                expanded = false
                                onValueChange(option)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock count field
            OutlinedTextField(
                value = stockCount,
                onValueChange = onStockCountChange,
                label = { Text("Number of Stocks") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = errorMessage?.contains("stock", ignoreCase = true) == true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price field
            OutlinedTextField(
                value = priceText,
                onValueChange = { newValue ->
                    if (!userHasTyped) {
                        userHasTyped = true
                    }
                    priceText = newValue
                    onPriceChange(newValue)
                },
                label = { Text("Price per Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                enabled = !indexUiState.isLoading,
                isError = errorMessage?.contains("price", ignoreCase = true) == true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Money,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    when {
                        indexUiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        indexUiState.error != null -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date picker field
            OutlinedTextField(
                value = formattedDate,
                onValueChange = { },
                label = { Text("Purchase Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                //enabled = false,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Date"
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
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
                listOf("1", "10", "50", "100","500").forEach { quantity ->
                    FilterChip(
                        selected = stockCount == quantity,
                        onClick = { onStockCountChange(quantity) },
                        label = { Text(quantity) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Confirm and Cancel buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onConfirm,
                    enabled = stockCount.isNotEmpty() && selectedDate != null && price.isNotEmpty()
                ) {
                    Text("Add")
                }
            }
        }
    }

    // Date Picker Dialog (remains the same)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateChange(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select Purchase Date",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun AddStockBottomSheetContent(
    searchUiState: SearchUiState,
    onTickerClick: (String, String) -> Unit,
    onDismiss: () -> Unit,
    listOfPortfolio: List<PortfolioModel>,
    onDelete: (String) -> Unit
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
                    listOfPortfolio = listOfPortfolio,
                    onDelete = onDelete
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
    listOfPortfolio: List<PortfolioModel>,
    onDelete: (String) -> Unit

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
                    listOfPortfolio = listOfPortfolio,
                    onDelete = { onDelete(item) }
                )
            }
        }
    }
}

@Composable
fun SymbolRow(
    symbol: String,
    onClick: () -> Unit,
    listOfPortfolio:  List<PortfolioModel>,
    onDelete: () -> Unit
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
            onAdd = onClick , onDelete = onDelete)
    }
}

@Composable
fun PortfolioActionButton(
    symbol: String,
    portfolioItems: List<PortfolioModel>,
    onAdd: () -> Unit,
    onDelete: () -> Unit,   // new callback for deletion
    modifier: Modifier = Modifier
) {
    val isInPortfolio = portfolioItems.any { it.symbol == symbol }

    Button(
        onClick = {
            if (isInPortfolio) {
                onDelete()   // trigger delete when already in portfolio
            } else {
                onAdd()      // trigger add when not in portfolio
            }
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isInPortfolio) {
                MaterialTheme.colorScheme.errorContainer   // delete uses error color
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = if (isInPortfolio) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    ) {
        Icon(
            imageVector = if (isInPortfolio) Icons.Default.Delete else Icons.Default.Add,
            contentDescription = if (isInPortfolio) "Delete from portfolio" else "Add to portfolio",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isInPortfolio) "Delete" else "Add",
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
    isHideSector: Boolean = true,
    onUpdateTicker:  ((Offset) -> Unit)
) {
    Card(

        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTickerClick() },
                    onLongPress = onUpdateTicker

                )
            }

            .fillMaxWidth(),
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
    onTickerClick: (String, String,Double) -> Unit,
    onUpdateTicker: (String, Int, Double) -> Unit,
    onAddTransactionClick: () -> Unit
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

            item{
                Text(
                    text = "Portfolio Allocation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp, top = 8.dp)
                )
            }



            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(6.dp),
                ) {
                    DonutChartWithLegend(
                        data = chartSampleData,
                    )
                }

            }
            item{
                Text(
                    text = "Your holdings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp, top = 8.dp)
                )
            }

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "${items.size} Transactions",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FilledTonalButton(
                        onClick = onAddTransactionClick,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Transaction")
                    }
                }
            }

            items(items) { item ->
                CompactWatchlistItemCard(
                    item = item,
                    onRemove = { onRemoveItem(item.data.symbol) },
                    onTickerClick = { onTickerClick("REG", item.data.symbol,item.data.price) },
                    onUpdateTicker = { onUpdateTicker(item.data.symbol,item.data.stockCount,item.data.price) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}