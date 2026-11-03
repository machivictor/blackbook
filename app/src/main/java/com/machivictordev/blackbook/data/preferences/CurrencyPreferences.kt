package com.machivictordev.blackbook.data.preferences

import android.content.Context
import com.machivictordev.blackbook.domain.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("black_book_currency", Context.MODE_PRIVATE)

    private val _selectedCurrency = MutableStateFlow(getSavedCurrency())
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

    fun getCurrency(): Currency = _selectedCurrency.value

    fun setCurrency(currencyCode: String) {
        prefs.edit().putString(KEY_CURRENCY_CODE, currencyCode).apply()
        _selectedCurrency.value = Currency.findByCode(currencyCode)
    }

    private fun getSavedCurrency(): Currency {
        val code = prefs.getString(KEY_CURRENCY_CODE, Currency.DEFAULT.code) ?: Currency.DEFAULT.code
        return Currency.findByCode(code)
    }

    companion object {
        private const val KEY_CURRENCY_CODE = "currency_code"
    }
}
