package com.machivictordev.blackbook.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machivictordev.blackbook.data.preferences.CurrencyPreferences
import com.machivictordev.blackbook.domain.model.Currency
import com.machivictordev.blackbook.ui.components.CurrencySelectionContent

@Composable
fun CurrencySelectionScreen(
    currencyPreferences: CurrencyPreferences,
    onFinish: () -> Unit,
) {
    var selectedCurrency by rememberSaveable(stateSaver = CurrencySaver) {
        mutableStateOf(currencyPreferences.getCurrency())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 24.dp, bottom = 16.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Select Preferred Currency",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose the currency you'll use for managing all debt and repayment records across Africa, America, Asia, and Europe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CurrencySelectionContent(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { selectedCurrency = it },
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    currencyPreferences.setCurrency(selectedCurrency.code)
                    onFinish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Save & Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private val CurrencySaver = androidx.compose.runtime.saveable.Saver<Currency, String>(
    save = { it.code },
    restore = { Currency.findByCode(it) },
)
