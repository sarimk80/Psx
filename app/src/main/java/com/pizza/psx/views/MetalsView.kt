package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.StockGreen
import com.pizza.compose.baraRed
import com.pizza.compose.financialGreen
import com.pizza.compose.financialGrey
import com.pizza.compose.financialWarning
import com.pizza.compose.hotStone
import com.pizza.compose.lavender
import com.pizza.compose.orange
import com.pizza.compose.purpleColor
import com.pizza.compose.rust
import com.pizza.compose.teal
import com.pizza.compose.veryBerry
import com.pizza.compose.veryBlue
import com.pizza.compose.yellow
import com.pizza.psx.R
import com.pizza.psx.domain.model.MetalList
import com.pizza.psx.presentation.viewModel.MetalViewModel

data class CommodityItem(
    val name: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalsView(
    onBackClick: () -> Unit,
    onClickMetal: (metal: String) -> Unit,
) {

    val viewModel: MetalViewModel = hiltViewModel()
    val uiMetalState = viewModel.uiMetalState.value

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        viewModel.getMetalList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Commodities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Precious metals & markets",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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

                // Loading
                uiMetalState.isLoading && uiMetalState.metalList == null -> {
                    CommodityLoading()
                }

                // Error
                uiMetalState.error != null -> {
                    CommodityError(
                        message = uiMetalState.error ?: "Something went wrong",
                        onRetry = {
                            viewModel.getMetalList()
                        }
                    )
                }

                // Success
                uiMetalState.metalList != null -> {

                    CommodityContent(
                        metalList = uiMetalState.metalList,
                        searchQuery = searchQuery,
                        onSearchChange = {
                            searchQuery = it
                        },
                        onClickMetal = onClickMetal
                    )
                }
            }
        }
    }
}

@Composable
private fun CommodityContent(
    metalList: MetalList,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClickMetal: (String) -> Unit
) {

    val metals = listOf(
        CommodityItem(
            name = "Gold",
            value = metalList.gold,
            icon = Icons.Default.AutoAwesome,
            color = yellow,
        ),
        CommodityItem(
            name = "Silver",
            value = metalList.silver,
            icon = Icons.Default.Circle,
            color = hotStone
        ),
        CommodityItem(
            name = "Platinum",
            value = metalList.platinum,
            icon = Icons.Default.Diamond,
            color = rust,
        ),
        CommodityItem(
            name = "Palladium",
            value = metalList.palladium,
            icon = Icons.Default.Hexagon,
            color = purpleColor
        ),
        CommodityItem(
            name = "Copper",
            value = metalList.copper,
            icon = Icons.Default.Cable,
            color = baraRed
        ),

        // Energy
        CommodityItem(
            name = "WTI Crude Oil",
            value = metalList.wtiCrudeOil,
            icon = Icons.Default.LocalGasStation,
            color = veryBerry
        ),
        CommodityItem(
            name = "Brent Crude Oil",
            value = metalList.brentCrudeOil,
            icon = Icons.Default.LocalGasStation,
            color = lavender
        ),
        CommodityItem(
            name = "Natural Gas",
            value = metalList.naturalGas,
            icon = Icons.Default.LocalFireDepartment,
            color = teal
        ),
        CommodityItem(
            name = "Gasoline",
            value = metalList.gasoline,
            icon = Icons.Default.LocalGasStation,
            color = veryBlue
        ),
        CommodityItem(
            name = "Heating Oil",
            value = metalList.heatingOil,
            icon = Icons.Default.LocalFireDepartment,
            color = orange
        ),

        // Grains
        CommodityItem(
            name = "Corn",
            value = metalList.corn,
            icon = Icons.Default.Eco,
            color = StockGreen
        ),
        CommodityItem(
            name = "Wheat",
            value = metalList.wheat,
            icon = Icons.Default.Grass,
            color = teal
        ),
        CommodityItem(
            name = "Soybeans",
            value = metalList.soybeans,
            icon = Icons.Default.Eco,
            color = financialGrey
        ),
        CommodityItem(
            name = "Oats",
            value = metalList.oats,
            icon = Icons.Default.Grass,
            color = lavender
        ),
        CommodityItem(
            name = "Rough Rice",
            value = metalList.roughRice,
            icon = Icons.Default.Grass,
            color = baraRed
        ),

        // Soft commodities
        CommodityItem(
            name = "Coffee",
            value = metalList.coffee,
            icon = Icons.Default.LocalCafe,
            color = yellow
        ),
        CommodityItem(
            name = "Sugar",
            value = metalList.sugar,
            icon = Icons.Default.Cake,
            color = financialGrey
        ),
        CommodityItem(
            name = "Cocoa",
            value = metalList.cocoa,
            icon = Icons.Default.Cookie,
            color = veryBlue,
        ),
        CommodityItem(
            name = "Cotton",
            value = metalList.cotton,
            icon = Icons.Default.Cloud,
            color = rust,
        ),
        CommodityItem(
            name = "Lumber",
            value = metalList.lumber,
            icon = Icons.Default.Forest,
            color = financialGreen
        ),
        CommodityItem(
            name = "Orange Juice",
            value = metalList.orangeJuice,
            icon = Icons.Default.LocalDrink,
            color = orange
        ),

        // Livestock
        CommodityItem(
            name = "Live Cattle",
            value = metalList.liveCattle,
            icon = Icons.Default.Pets,
            color = hotStone
        ),
        CommodityItem(
            name = "Feeder Cattle",
            value = metalList.feederCattle,
            icon = Icons.Default.EmojiNature,
            color = yellow
        ),
        CommodityItem(
            name = "Lean Hogs",
            value = metalList.leanHogs,
            icon = Icons.Default.Pets,
            color = StockGreen
        )
    )



    val filteredMetals = metals.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text("Search commodities")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (filteredMetals.isNotEmpty()) {

            item {
                SectionMetalTitle()
            }

            items(
                items = filteredMetals,
                key = { it.name }
            ) { commodity ->
                CommodityCard(
                    commodity = commodity,
                    onClick = {
                        onClickMetal(commodity.value)
                    }
                )
            }
        }

        if (
            filteredMetals.isEmpty()
        ) {

            item {
                EmptySearchState()
            }
        }

        item {
            Spacer(modifier = Modifier.height(68.dp))
        }
    }
}

@Composable
private fun CommodityCard(
    commodity: CommodityItem,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        //shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
        elevation = CardDefaults.cardElevation(18.dp),

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        commodity.color.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = commodity.icon,
                    contentDescription = null,
                    tint = commodity.color
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = commodity.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "View market details",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }
    }
}

@Composable
private fun SectionMetalTitle() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 14.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Precious Metals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )


    }
}

@Composable
private fun CommodityLoading() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading commodities...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CommodityError(
    message: String,
    onRetry: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Unable to load commodities",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Try Again")
        }
    }
}

@Composable
private fun EmptySearchState() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No commodities found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Try a different search term.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}