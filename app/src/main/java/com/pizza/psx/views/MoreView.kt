package com.pizza.psx.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizza.compose.financialGreen
import com.pizza.compose.financialRed
import com.pizza.compose.financialWarning
import com.pizza.psx.presentation.viewModel.MoreViewModel
import com.pizza.psx.presentation.viewModel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreView(
    onSearchClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onClearDataClick: () -> Unit = {},

) {
    val viewModel: MoreViewModel = hiltViewModel()

    val showClearDialog = remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & More",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }


            )
        },
    ) { paddingValues ->

        if (showClearDialog.value) {
            AlertDialog(
                onDismissRequest = { showClearDialog.value = false },
                title = {
                    Text("Clear All Data?")
                },
                text = {
                    Text("This action will permanently delete all your portfolio and transaction data. This cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDialog.value = false
                            viewModel.clearAllData()
                        }
                    ) {
                        Text(
                            "Delete",
                            color = financialRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearDialog.value = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = paddingValues.calculateTopPadding() + 12.dp,
                bottom = paddingValues.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            item {
                SettingsGroup(title = "Explore") {
                    SettingsRow(
                        icon = Icons.Outlined.Search,
                        iconTint = financialGreen,
                        label = "Search",
                        onClick = onSearchClick
                    )

//                    HorizontalDivider(
//                        color = MaterialTheme.colorScheme.outlineVariant
//                    )
//
//                    SettingsRow(
//                        icon = Icons.Outlined.SwitchLeft,
//                        iconTint = purpleColor,
//                        label = "Metals",
//                        onClick = onSearchClick
//                    )
//
//                    HorizontalDivider(
//                        color = MaterialTheme.colorScheme.outlineVariant
//                    )
//
//                    SettingsRow(
//                        icon = Icons.Outlined.CurrencyExchange,
//                        iconTint = green,
//                        label = "Currency Exchange",
//                        onClick = onSearchClick
//                    )
//
//                    HorizontalDivider(
//                        color = MaterialTheme.colorScheme.outlineVariant
//                    )
//
//                    SettingsRow(
//                        icon = Icons.Outlined.Bolt,
//                        iconTint = veryBerry,
//                        label = "Circuit Breaker",
//                        onClick = onSearchClick
//                    )

                }
            }

            item {
                SettingsGroup(title = "Privacy") {
                    SettingsRow(
                        icon = Icons.Outlined.Shield,
                        iconTint = financialWarning,
                        label = "Privacy Policy",
                        onClick = onPrivacyPolicyClick
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SettingsRow(
                        icon = Icons.Outlined.DeleteOutline,
                        iconTint = financialRed,
                        label = "Clear All Data",
                        labelColor = financialRed,
                        onClick = { showClearDialog.value = true },
                        showChevron = false
                    )
                }
            }

            item {
                // ✅ uses BuildConfig instead of hardcoded strings
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Stokistan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = "v-19 (build 2.8)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 1.dp,
            shadowElevation = 0.dp
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    labelColor: Color = Color.Unspecified,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (labelColor == Color.Unspecified)
                MaterialTheme.colorScheme.onSurface
            else labelColor,
            modifier = Modifier.weight(1f)
        )

        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}