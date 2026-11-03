package com.machivictordev.blackbook.ui.people

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.machivictordev.blackbook.R
import com.machivictordev.blackbook.domain.model.Debtor
import com.machivictordev.blackbook.domain.model.DebtorAccount
import com.machivictordev.blackbook.ui.components.AddPersonDialog
import com.machivictordev.blackbook.ui.components.AppListItem
import com.machivictordev.blackbook.ui.util.LocalCurrency
import com.machivictordev.blackbook.ui.util.formatMoney
import com.machivictordev.blackbook.ui.util.formatRelativeDate

@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel,
    observeDebtorsForSuggestions: @Composable () -> List<Debtor>,
    onOpenDebtor: (Long) -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add contact",
                    )
                }
            },
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
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Main Content Sheet
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Search Bar
                            TextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = { Text("Search name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(32.dp),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent
                                )
                            )

                            val filteredDebtors = remember(state.debtors, state.searchQuery) {
                                if (state.searchQuery.isBlank()) {
                                    state.debtors.sortedBy { it.name }
                                } else {
                                    state.debtors.filter {
                                        it.name.contains(state.searchQuery, ignoreCase = true)
                                    }
                                }
                            }

                            if (filteredDebtors.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                                    contentAlignment = Alignment.TopCenter,
                                ) {
                                    Text(
                                        text = if (state.searchQuery.isNotEmpty()) "No matching people found." else "No people in records.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            } else {

                                Spacer(modifier = Modifier.size(8.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    items(filteredDebtors, key = { it.id }) { debtor ->
                                        PersonListItem(
                                            debtor = debtor,
                                            onClick = { onOpenDebtor(debtor.id) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            val suggestions = observeDebtorsForSuggestions()
            AddPersonDialog(
                isSaving = state.isSaving,
                existingDebtors = suggestions,
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    viewModel.addPerson(name)
                    showAddDialog = false
                },
            )
        }
    }
}

@Composable
private fun PersonListItem(
    debtor: DebtorAccount,
    onClick: () -> Unit,
) {
    val currency = LocalCurrency.current
    val hasDebt = debtor.balanceMinor > 0L

    AppListItem(
        onClick = { onClick() },
        labelText = debtor.name,
        supportingText = formatRelativeDate(debtor.lastActivityAtMillis),
        trailingText = if (hasDebt)
        { formatMoney(debtor.balanceMinor, currency.symbol) }
        else { null },
        trailingSurfaceContainerColor = if (hasDebt) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        trailingTextColor = if (hasDebt) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        }
    )
}
