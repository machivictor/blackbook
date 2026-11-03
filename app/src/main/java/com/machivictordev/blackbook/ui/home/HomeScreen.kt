package com.machivictordev.blackbook.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.machivictordev.blackbook.R
import com.machivictordev.blackbook.domain.model.Debtor
import com.machivictordev.blackbook.domain.model.DebtorAccount
import com.machivictordev.blackbook.ui.components.AddDebtorDialog
import com.machivictordev.blackbook.ui.components.AppListItem
import com.machivictordev.blackbook.ui.components.Dashboard
import com.machivictordev.blackbook.ui.util.LocalCurrency
import com.machivictordev.blackbook.ui.util.formatMoney
import com.machivictordev.blackbook.ui.util.formatRelativeDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    observeDebtorsForSuggestions: @Composable () -> List<Debtor>,
    onOpenDebtor: (Long) -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showSortBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add person",
                )
            }
        }
    ) { scaffoldPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val baseList = state.overview.activeDebtors
            val debtorsList = remember(baseList, state.searchQuery, state.sortOption) {
                val filtered = if (state.searchQuery.isBlank()) {
                    baseList
                } else {
                    baseList.filter {
                        it.name.contains(state.searchQuery, ignoreCase = true)
                    }
                }
                when (state.sortOption) {
                    HomeSortOption.NEWEST_DATE -> filtered.sortedByDescending { it.lastActivityAtMillis }
                    HomeSortOption.OLDEST_DATE -> filtered.sortedBy { it.lastActivityAtMillis }
                    HomeSortOption.HIGHEST_AMOUNT -> filtered.sortedByDescending { it.balanceMinor }
                    HomeSortOption.LOWEST_AMOUNT -> filtered.sortedBy { it.balanceMinor }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "dashboard") {
                    Dashboard(
                        totalOutstandingMinor = state.overview.totalOutstandingMinor,
                        totalDueMinor = state.overview.totalDueMinor,
                        totalCollectedMinor = state.overview.totalCollectedMinor,
                        activeDebtsCount = state.overview.activeDebtors.size
                    )
                }


                item(key = "section_header") {
                    Spacer(modifier = Modifier.size(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Debts to follow up",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { showSortBottomSheet = true },
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .padding(horizontal = 4.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor =  MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = "Sort debts",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sort",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                if (debtorsList.isEmpty()) {
                    item(key = "empty_state") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Text(
                                text = if (state.searchQuery.isNotEmpty()) "No matching debtors found." else "No active debts.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    item(key = "debtors_list_container") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            debtorsList.forEach { debtor ->
                                DebtorListItem(
                                    debtor = debtor,
                                    onClick = { onOpenDebtor(debtor.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showAddDialog) {
            val suggestions = observeDebtorsForSuggestions()
            AddDebtorDialog(
                isSaving = state.isSaving,
                existingDebtors = suggestions,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, amountMinor, note ->
                    viewModel.addDebtor(name, amountMinor, note)
                    showAddDialog = false
                },
            )
        }

        if (showSortBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSortBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                ) {
                    Text(
                        text = "Sort debts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HomeSortOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateSortOption(option)
                                    scope.launch {
                                        sheetState.hide()
                                        showSortBottomSheet = false
                                    }
                                }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            RadioButton(
                                selected = state.sortOption == option,
                                onClick = {
                                    viewModel.updateSortOption(option)
                                    scope.launch {
                                        sheetState.hide()
                                        showSortBottomSheet = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtorListItem(
    debtor: DebtorAccount,
    onClick: () -> Unit
) {

    AppListItem(
        onClick = { onClick() },
        labelText = debtor.name,
        supportingText = formatRelativeDate(debtor.lastActivityAtMillis),
        trailingText = formatMoney(debtor.balanceMinor, LocalCurrency.current.symbol)
    )

//    val currency = LocalCurrency.current

//    ListItem(
//        modifier = Modifier
//            .clip(RoundedCornerShape(16.dp))
//            .clickable { onClick() },
//        colors = ListItemDefaults.colors(
//            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
//        ),
//        leadingContent = {
//            Surface(
//                modifier = Modifier.size(40.dp),
//                shape = CircleShape,
//                color = MaterialTheme.colorScheme.secondaryContainer
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    Icon(
//                        painter = painterResource(R.drawable.person),
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                }
//            }
//        },
//        headlineContent = {
//            Text(
//                text = formatMoney(debtor.balanceMinor, currency.symbol),
//                style = MaterialTheme.typography.bodyLarge.copy(
//                    fontSize = 18.sp
//                ),
//                color = MaterialTheme.colorScheme.onSurface,
//                modifier = Modifier.padding(bottom = 2.dp)
//            )
//        },
//        supportingContent = {
//            Text(
//                text = debtor.name,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//            )
//        },
//        trailingContent = {
//            Text(
//                text = formatRelativeDate(debtor.lastActivityAtMillis),
//                style = MaterialTheme.typography.labelLarge,
//                color = MaterialTheme.colorScheme.outline,
//            )
//        }
//    )
}
