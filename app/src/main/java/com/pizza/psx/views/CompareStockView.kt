package com.pizza.psx.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.psx.R
import com.pizza.psx.presentation.helpers.StockFabShape
import com.pizza.psx.presentation.viewModel.CompareStockViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CompareStockView() {

    val viewModel: CompareStockViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    var showBottomSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val selectedTickers = remember { mutableStateListOf<String>() }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) { viewModel.getSymbolList() }

    // Reset search whenever the sheet is dismissed so it opens fresh next time
    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet) searchQuery = ""
    }

    val filteredSymbols by remember(searchQuery, uiState.symbolList) {
        derivedStateOf {
            val all = uiState.symbolList?.data.orEmpty()
            if (searchQuery.isBlank()) all
            else all.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Compare Stocks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },

                )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(bottom = 60.dp)
                    .navigationBarsPadding()
                    .navigationBarsPadding()
                    .navigationBarsPadding()
                    .navigationBarsPadding(),
                onClick = { showBottomSheet = true },
                shape = StockFabShape,
            ) {
                Icon(Icons.Default.Search, contentDescription = "Add stock to compare")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (selectedTickers.isEmpty()) {
                // Centered empty state instead of top-left, orphaned content
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.undraw_no_data_ig65),
                        contentDescription = null,
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "No stocks selected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Tap the search button below to add stocks you'd like to compare",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "${selectedTickers.size} selected",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    // Chips read naturally as a horizontal row of tags rather than
                    // a full-width vertical list
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(selectedTickers.size) { index ->
                            val ticker = selectedTickers[index]
                            FilterChip(
                                modifier = Modifier.padding(vertical = 6.dp),
                                selected = true,
                                onClick = {
                                    selectedTickers.remove(ticker)
                                    showBottomSheet = false
                                          },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Remove $ticker"
                                    )
                                }
                            ) {
                                Text(ticker)
                            }
                        }
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxWidth(),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select tickers",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showBottomSheet = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp),
                            placeholder = { Text("Search symbol") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true
                        )

                        when {
                            uiState.symbolList == null -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            filteredSymbols.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No symbols match \"$searchQuery\"",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding()
                                ) {
                                    items(filteredSymbols.size) { index ->
                                        val symbol = filteredSymbols[index]
                                        val isSelected = selectedTickers.contains(symbol)
                                        SymbolListItem(
                                            symbol = symbol,
                                            onClick = {
                                                // Toggling instead of only adding lets users
                                                // deselect directly from the picker
                                                if (isSelected) {
                                                    selectedTickers.remove(symbol)
                                                } else {
                                                    selectedTickers.add(symbol)
                                                }
                                            },
                                            isShowIcon = isSelected,
                                            isTicker = isSelected
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}