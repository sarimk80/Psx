package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.veryBlue
import com.pizza.psx.R
import com.pizza.psx.domain.model.CurrencyResponse
import com.pizza.psx.presentation.helpers.number_format
import com.pizza.psx.presentation.viewModel.CurrencyExchangeViewModel
import androidx.compose.material.icons.filled.SwapVert
 import androidx.compose.material3.ExposedDropdownMenuBox
 import androidx.compose.ui.text.input.KeyboardType
 import androidx.compose.material3.OutlinedTextFieldDefaults
 import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction


private val regionFilters = listOf("All", "Asia", "Europe", "Americas", "Africa", "Middle East","Crypto","Metals","Oceania")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyChangeView(onBackClick: () -> Unit) {
    val viewModel: CurrencyExchangeViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCurrency by remember {mutableStateOf<CurrencyResponse?>(null)}
    var currencyAmount by remember { mutableStateOf(1) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) { viewModel.getAllCurrency() }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Currency Exchange",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                            )
                        }
                    },

                )
            }
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
                Icon(Icons.Default.CurrencyExchange, "Add stock")
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding),
            contentAlignment = Alignment.Center
        ) {


            when {
                uiState.isLoading && uiState.currency == null -> BreakerLoadingState()
                uiState.error != null -> BreakerErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.getAllCurrency() }
                )
                uiState.currency != null -> {

                    val filtered = uiState.currency!!.response.filter {
                        val matchesSearch = it.country.contains(searchQuery, ignoreCase = true) ||
                                it.currencyName.contains(searchQuery, ignoreCase = true)

                        val matchesFilter = selectedFilter == "All" ||
                                it.type.equals(selectedFilter, ignoreCase = true)
//                                ||
//                                (selectedFilter == "Mideast" && it.type.equals("Middle East", ignoreCase = true))

                        matchesSearch && matchesFilter
                    }

                    CurrencyContent(
                        currencies = filtered,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedFilter = selectedFilter,
                        onFilterChange = { selectedFilter = it  },
                        onClick = { item ->
                            selectedCurrency = item
                            showBottomSheet = true
                        }
                    )
                }
            }
            if(showBottomSheet && uiState.currency != null) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxWidth(),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    ExchangeBottomSheet(uiState.currency!!.response,
                        onDismiss = {showBottomSheet = false },
                        selectedCurrency = selectedCurrency
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExchangeBottomSheet(
    currencies: List<CurrencyResponse>,
    onDismiss: () -> Unit,
    selectedCurrency: CurrencyResponse?,
) {
    var fromCurrency by remember { mutableStateOf(selectedCurrency ?: currencies.getOrNull(0)) }
    var toCurrency by remember { mutableStateOf(currencies.getOrNull(1) ?: currencies.getOrNull(0)) }
    var amount by remember { mutableStateOf("1") }
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }


    val amountValue = amount.toDoubleOrNull() ?: 0.0

    val keyboardController = LocalSoftwareKeyboardController.current


    val convertedAmount =
        if (fromCurrency != null && toCurrency != null) {
            amountValue *
                    fromCurrency!!.currency /
                    toCurrency!!.currency
        } else {
            0.0
        }

    val rate =
        if (fromCurrency != null && toCurrency != null) {
            fromCurrency!!.currency / toCurrency!!.currency
        } else {
            0.0
        }

    if (showFromPicker) {
        CurrencyPickerDialog(
            currencies = currencies,
            onDismiss = {
                showFromPicker = false
            },
            onCurrencySelected = {
                fromCurrency = it
            }
        )
    }

    if (showToPicker) {
        CurrencyPickerDialog(
            currencies = currencies,
            onDismiss = {
                showToPicker = false
            },
            onCurrencySelected = {
                toCurrency = it
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp,vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Exchange Currency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                )
            }
        }


        // From Currency
        Text(
            text = "From",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable {
                    showFromPicker = true
                }
        ) {
            OutlinedTextField(
                value = fromCurrency?.currencyName ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                textStyle = TextStyle(fontSize = 11.sp),
                interactionSource = remember { MutableInteractionSource() },
                label = { Text("Select currency") },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
                trailingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier

                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),

                )
        }



        // Amount Input
        Text(
            text = "Amount",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),


            leadingIcon = {
                Text(
                    text = fromCurrency?.currencyName ?: "",
                    modifier = Modifier.padding(start = 12.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        )

        // Swap Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FilledIconButton(
                onClick = {
                    val temp = fromCurrency
                    fromCurrency = toCurrency
                    toCurrency = temp
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.SwapVert,
                    null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // To Currency
        Text(
            text = "To",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable {
                    showToPicker = true
                }
        ) {
            OutlinedTextField(
                value = toCurrency?.currencyName ?: "",
                onValueChange = {},
                textStyle = TextStyle(fontSize = 11.sp),
                readOnly = true,
                enabled = false,
                label = { Text("Select currency") },
                interactionSource = remember { MutableInteractionSource() },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),

                )
        }



        // Converted Amount Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
        ) {
            Column(
                modifier = Modifier.padding(11.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "You get",
                        fontSize = 13.sp,
                    )
                    Text(
                        text = "${number_format(convertedAmount)} ${toCurrency?.currencyName ?: ""}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = "1 ${fromCurrency?.currencyName} = ${number_format(rate)}",
                    fontSize = 12.sp,
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerDialog(
    currencies: List<CurrencyResponse>,
    onDismiss: () -> Unit,
    onCurrencySelected: (CurrencyResponse) -> Unit
) {
    var search by remember { mutableStateOf("") }

    val filteredCurrencies = remember(search, currencies) {
        currencies.filter {
            it.currencyName.contains(search, ignoreCase = true) ||
                    it.country.contains(search, ignoreCase = true)
        }
    }
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,

    ){
        Column {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                placeholder = {
                    Text("Search currency...")
                },

            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
            ) {
                items(
                    items = filteredCurrencies,
                    key = { it.currencyName }
                ) { currency ->

                    ListItem(
                        headlineContent = {
                            Text(currency.currencyName)
                        },
                        supportingContent = {
                            Text(currency.country)
                        },
                        trailingContent = {
                            Text(
                                number_format(currency.currency)
                            )
                        },
                        modifier = Modifier.clickable {
                            onCurrencySelected(currency)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }

}


@Composable
private fun CurrencyContent(
    currencies: List<CurrencyResponse>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onClick: (CurrencyResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = {
                    Text("Search currency or country…", color = Color(0xFF44445A), fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),

            )
        }



        // Region filter chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(regionFilters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter, fontSize = 12.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(

                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == filter,
                            borderWidth = 0.5.dp,
                            selectedBorderWidth = 0.5.dp
                        )
                    )
                }
            }
        }

        // Section label
        item {
            Text(
                text = "LIVE RATES",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.08.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Currency cards
        items(currencies) { currency ->
            CurrencyCard(
                item = currency,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                onClick = onClick
            )
        }

        if (currencies.isEmpty()) {
            item { EmptyState() }
        }

        item{
            Spacer(modifier = Modifier.padding(vertical = 36.dp))
        }
    }
}

@Composable
fun CurrencyCard(
    item: CurrencyResponse,
    modifier: Modifier = Modifier,
    onClick: (CurrencyResponse) -> Unit
) {


    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onClick(item) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Flag / avatar circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.currencyName.take(3).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Country info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.country,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.currencyName} - ${item.type}",
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Rate + change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = number_format(item.currency),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {

                    Text(text = "1 ${item.currencyName} = ${number_format(item.currency)} PKR", fontSize = 11.sp, color = veryBlue)
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No currencies found", fontSize = 14.sp)
    }
}