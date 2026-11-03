package com.machivictordev.blackbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.machivictordev.blackbook.domain.model.Debtor
import com.machivictordev.blackbook.domain.util.MoneyParser
import com.machivictordev.blackbook.ui.util.LocalCurrency
import com.machivictordev.blackbook.ui.util.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtorDialog(
    isSaving: Boolean,
    existingDebtors: List<Debtor> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (name: String, amountMinor: Long, note: String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }
    var amountError by rememberSaveable { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val currency = LocalCurrency.current
    val prefixText = if (currency.symbol.endsWith(".")) "${currency.symbol} " else "${currency.symbol} "

    val filteredSuggestions = remember(name, existingDebtors) {
        if (name.isBlank()) emptyList()
        else existingDebtors.filter { it.name.contains(name, ignoreCase = true) && !it.name.equals(name, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Who owes you money?") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded && filteredSuggestions.isNotEmpty(),
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                            expanded = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        isError = nameError,
                        supportingText = if (nameError) ({ Text("Enter a name.") }) else null,
                    )
                    ExposedDropdownMenu(
                        expanded = expanded && filteredSuggestions.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize(),
                    ) {
                        filteredSuggestions.forEach { debtor ->
                            DropdownMenuItem(
                                text = { Text(debtor.name) },
                                onClick = {
                                    name = debtor.name
                                    expanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount") },
                    singleLine = true,
                    prefix = { Text(prefixText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = if (amountError) ({ Text("Enter a positive amount, up to 2 decimal places.") }) else null,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    val parsedAmount = MoneyParser.parseMinorUnits(amountText)
                    nameError = name.isBlank()
                    amountError = parsedAmount == null
                    if (!nameError && !amountError && parsedAmount != null) {
                        onConfirm(name.trim(), parsedAmount, note.trim())
                    }
                },
            ) { Text(if (isSaving) "Saving…" else "Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancel") } },
    )
}

@Composable
fun TransactionDialog(
    title: String,
    maxAmountMinor: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (isDebt: Boolean, amountMinor: Long, note: String) -> Unit,
) {
    var isDebt by rememberSaveable { mutableStateOf(true) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var amountError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("Enter a positive amount, up to 2 decimal places.") }

    val currency = LocalCurrency.current
    val prefixText = if (currency.symbol.endsWith(".")) "${currency.symbol} " else "${currency.symbol} "

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = isDebt,
                        onClick = { isDebt = true },
                        label = { Text("Debt") },
                        leadingIcon = if (isDebt) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    FilterChip(
                        selected = !isDebt,
                        onClick = { isDebt = false },
                        label = { Text("Payment") },
                        leadingIcon = if (!isDebt) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount") },
                    prefix = { Text(prefixText) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = if (amountError) ({ Text(errorMessage) }) else null,
                )
                if (!isDebt && maxAmountMinor != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Outstanding balance: ${formatMoney(maxAmountMinor, currency.symbol)}")
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedAmount = MoneyParser.parseMinorUnits(amountText)
                if (parsedAmount == null) {
                    amountError = true
                    errorMessage = "Enter a positive amount, up to 2 decimal places."
                } else if (!isDebt && maxAmountMinor != null && parsedAmount > maxAmountMinor) {
                    amountError = true
                    errorMessage = "Payment cannot exceed the outstanding balance."
                } else {
                    onConfirm(isDebt, parsedAmount, note.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun ChangeNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(currentName) }
    var nameError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                singleLine = true,
                isError = nameError,
                supportingText = if (nameError) ({ Text("Enter a valid name.") }) else null,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onConfirm(name.trim())
                    }
                },
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun AmountEntryDialog(
    title: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (amountMinor: Long) -> Unit,
    maxAmountMinor: Long? = null,
) {
    var amountText by rememberSaveable { mutableStateOf("") }
    var amountError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("Enter a positive amount, up to 2 decimal places.") }

    val currency = LocalCurrency.current
    val prefixText = if (currency.symbol.endsWith(".")) "${currency.symbol} " else "${currency.symbol} "

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount") },
                    prefix = { Text(prefixText) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    supportingText = if (amountError) ({ Text(errorMessage) }) else null,
                )
                if (maxAmountMinor != null) {
                    Spacer(Modifier.height(4.dp))
                    Text("Outstanding balance: ${formatMoney(maxAmountMinor, currency.symbol)}")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedAmount = MoneyParser.parseMinorUnits(amountText)
                if (parsedAmount == null) {
                    amountError = true
                    errorMessage = "Enter a positive amount, up to 2 decimal places."
                } else if (maxAmountMinor != null && parsedAmount > maxAmountMinor) {
                    amountError = true
                    errorMessage = "Payment cannot exceed the outstanding balance."
                } else {
                    onConfirm(parsedAmount)
                }
            }) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonDialog(
    isSaving: Boolean,
    existingDebtors: List<Debtor> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("Enter a name.") }
    var expanded by remember { mutableStateOf(false) }

    val trimmedName = name.trim()
    val isDuplicate = remember(trimmedName, existingDebtors) {
        if (trimmedName.isBlank()) false
        else existingDebtors.any { it.name.equals(trimmedName, ignoreCase = true) }
    }

    val filteredSuggestions = remember(name, existingDebtors) {
        if (name.isBlank()) emptyList()
        else existingDebtors.filter { it.name.contains(name, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add contact") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded && filteredSuggestions.isNotEmpty(),
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                            expanded = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        isError = nameError || isDuplicate,
                        supportingText = when {
                            nameError -> ({ Text(errorMessage) })
                            isDuplicate -> ({ Text("A person with this name already exists.") })
                            else -> null
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = expanded && filteredSuggestions.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize(),
                    ) {
                        filteredSuggestions.forEach { debtor ->
                            DropdownMenuItem(
                                text = { Text(debtor.name) },
                                onClick = {
                                    name = debtor.name
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    val clean = name.trim()
                    if (clean.isEmpty()) {
                        nameError = true
                        errorMessage = "Enter a name."
                    } else if (existingDebtors.any { it.name.equals(clean, ignoreCase = true) }) {
                        nameError = true
                        errorMessage = "A person with this name already exists."
                    } else {
                        onConfirm(clean)
                    }
                },
            ) { Text(if (isSaving) "Saving…" else "Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancel") } },
    )
}
