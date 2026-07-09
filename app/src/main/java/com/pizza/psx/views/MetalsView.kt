package com.pizza.psx.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.pizza.psx.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalsView(
    onBackClick: () -> Unit,
    onClickMetal:(metal:String) -> Unit,
) {

    val metals = listOf(
        "Gold",
        "Silver",
        "Copper",
        "Platinum",
        "Palladium"
    )

    fun getMetalIcon(metal: String) = when (metal) {
        "Gold" -> Icons.Default.Star
        "Silver" -> Icons.Default.Diamond
        "Copper" -> Icons.Default.Hexagon
        "Platinum" -> Icons.Default.AutoAwesome
        "Palladium" -> Icons.Default.Workspaces
        else -> Icons.Default.Circle
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Precious Metals",
                        style = MaterialTheme.typography.titleLarge
                    )
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
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "Available Metals",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Track and monitor metal prices",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(metals) { metal ->

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
                    onClick = {
                        onClickMetal(metal.lowercase())
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Icon(
                            imageVector = getMetalIcon(metal),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Column {
                            Text(
                                text = metal,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "View latest market information",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}