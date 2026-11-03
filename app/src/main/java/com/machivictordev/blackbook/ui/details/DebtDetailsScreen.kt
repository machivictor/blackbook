package com.machivictordev.blackbook.ui.details

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.machivictordev.blackbook.domain.model.DebtorDetails
import com.machivictordev.blackbook.ui.components.AppListItem
import com.machivictordev.blackbook.ui.components.ChangeNameDialog
import com.machivictordev.blackbook.ui.components.TransactionDialog
import com.machivictordev.blackbook.ui.util.LocalCurrency
import com.machivictordev.blackbook.ui.util.formatMoney
import com.machivictordev.blackbook.ui.util.formatShortDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailsScreen(
    viewModel: DebtDetailsViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val details = state.details
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var showChangeNameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }
    var showTransactionDialog by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteType by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            if (details?.debtor?.name?.isNotEmpty() == true) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Text(
                            text = details.debtor.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Change name") },
                                    onClick = {
                                        showMenu = false
                                        showChangeNameDialog = true
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteAccountDialog = true
                                    },
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (details != null) {
                FloatingActionButton(
                    onClick = { showTransactionDialog = true },
                    shape = RoundedCornerShape(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Add transaction",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    ) { scaffoldPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            details == null -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(24.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Account not found", style = MaterialTheme.typography.titleLarge)
                Text("This debt account may have been deleted.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            else -> {
                val transactions = remember(details) {
                    val list = mutableListOf<TransactionItem>()
                    details.debts.forEach { list.add(DebtTransaction(it)) }
                    details.repayments.forEach { list.add(PaymentTransaction(it)) }
                    list.sortedWith(compareByDescending<TransactionItem> { it.timestampMillis }.thenByDescending { it.id })
                }
                val currency = LocalCurrency.current

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "summary_card") {
                        DebtorSummaryCard(details = details)
                    }

                    item(key = "section_header") {
                        Text(
                            text = "Transaction history",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                        )
                    }

                    if (transactions.isEmpty()) {
                        item(key = "no-transactions") {
                            Text(
                                text = "No transactions yet.",
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        item(key = "transactions_container") {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                transactions.forEach { tx ->
                                    AppListItem(
                                        onClick = {
                                            if (tx.isDebt) {
                                                pendingDeleteType = "debt"
                                                pendingDeleteId = tx.id
                                            } else {
                                                pendingDeleteType = "payment"
                                                pendingDeleteId = tx.id
                                            }
                                        },
                                        labelText = tx.note,
                                        supportingText = formatShortDate(tx.timestampMillis),
                                        trailingText = formatMoney(tx.amountMinor, currency.symbol),
                                        showChevron = false,
                                        showLeadingIcon = false,
                                        showTrailingSurface = false,
                                        trailingTextColor = if (tx.isDebt) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showChangeNameDialog && details != null) {
        ChangeNameDialog(
            currentName = details.debtor.name,
            onDismiss = { showChangeNameDialog = false },
            onConfirm = { newName ->
                showChangeNameDialog = false
                viewModel.updateName(newName, onSuccess = {})
            },
        )
    }

    if (showDeleteAccountDialog && details != null) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete account?") },
            text = { Text("Are you sure you want to delete ${details.debtor.name} and all associated records?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteDebtor(onSuccess = onBack)
                    },
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (pendingDeleteType != null && pendingDeleteId != null) {
        val isDebt = pendingDeleteType == "debt"
        AlertDialog(
            onDismissRequest = {
                pendingDeleteType = null
                pendingDeleteId = null
            },
            title = { Text(if (isDebt) "Delete debt record?" else "Delete payment record?") },
            text = { Text("Are you sure you want to delete this entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = pendingDeleteId!!
                        if (isDebt) {
                            viewModel.deleteDebt(id)
                        } else {
                            viewModel.deleteRepayment(id)
                        }
                        pendingDeleteType = null
                        pendingDeleteId = null
                    },
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingDeleteType = null
                        pendingDeleteId = null
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showTransactionDialog && details != null) {
        TransactionDialog(
            title = "Add transaction",
            maxAmountMinor = details.balanceMinor,
            onDismiss = { showTransactionDialog = false },
            onConfirm = { isDebt, amount, note ->
                showTransactionDialog = false
                val finalNote = note.ifBlank { if (isDebt) "Debt" else "Paid back" }
                if (isDebt) {
                    viewModel.addDebt(amount, finalNote)
                } else {
                    viewModel.recordRepayment(amount, finalNote)
                }
            },
        )
    }
}

sealed interface TransactionItem {
    val id: Long
    val timestampMillis: Long
    val amountMinor: Long
    val note: String
    val isDebt: Boolean
}

data class DebtTransaction(val entry: com.machivictordev.blackbook.domain.model.DebtEntry) : TransactionItem {
    override val id = entry.id
    override val timestampMillis = entry.timestampMillis
    override val amountMinor = entry.amountMinor
    override val note = entry.note.ifBlank { "Debt" }
    override val isDebt = true
}

data class PaymentTransaction(val entry: com.machivictordev.blackbook.domain.model.RepaymentEntry) : TransactionItem {
    override val id = entry.id
    override val timestampMillis = entry.timestampMillis
    override val amountMinor = entry.amountMinor
    override val note = entry.note.ifBlank { "Paid back" }
    override val isDebt = false
}

@Composable
fun DebtorSummaryCard(
    details: DebtorDetails,
) {
    val currency = LocalCurrency.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (details.balanceMinor > 0) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = "Payment due",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${details.debtor.name} owes you",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = formatMoney(details.balanceMinor, currency.symbol),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Total debt given",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = formatMoney(details.totalDebtMinor, currency.symbol),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp
                            )
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(32.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Total payments",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = formatMoney(details.totalRepaidMinor, currency.symbol),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
