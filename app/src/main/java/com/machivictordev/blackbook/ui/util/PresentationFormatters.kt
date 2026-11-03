package com.machivictordev.blackbook.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.machivictordev.blackbook.domain.model.Currency
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

val LocalCurrency = staticCompositionLocalOf { Currency.DEFAULT }

private val wholeNumberFormat: NumberFormat = NumberFormat.getIntegerInstance(Locale.US)
private val shortDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d yyyy", Locale.getDefault())
private val longDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

fun formatMoney(amountMinor: Long, currencySymbol: String = Currency.DEFAULT.symbol): String {
    val nonNegative = amountMinor.coerceAtLeast(0L)
    val whole = nonNegative / 100L
    val cents = nonNegative % 100L
    val wholeText = wholeNumberFormat.format(whole)
    val amount = if (cents == 0L) wholeText else "$wholeText.${cents.toString().padStart(2, '0')}"
    return if (currencySymbol.endsWith(".")) "$currencySymbol $amount" else "$currencySymbol$amount"
}

fun formatMoney(amountMinor: Long, currency: Currency): String {
    return formatMoney(amountMinor, currency.symbol)
}

fun formatShortDate(timestampMillis: Long): String =
    Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault()).format(shortDateFormatter)

fun formatLongDate(timestampMillis: Long): String =
    Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault()).format(longDateFormatter)

fun formatRelativeDate(
    timestampMillis: Long,
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val zone = ZoneId.systemDefault()
    val targetDate = Instant.ofEpochMilli(timestampMillis).atZone(zone).toLocalDate()
    val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()

    val days = ChronoUnit.DAYS.between(targetDate, nowDate)

    return when {
        days <= 0L -> "Today"
        days == 1L -> "Yesterday"
        days in 2L..6L -> "$days days ago"
        days in 7L..29L -> {
            val weeks = days / 7L
            if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
        }
        days in 30L..364L -> {
            val months = ChronoUnit.MONTHS.between(targetDate, nowDate).coerceAtLeast(1L)
            if (months == 1L) "1 month ago" else "$months months ago"
        }
        else -> {
            val years = ChronoUnit.YEARS.between(targetDate, nowDate).coerceAtLeast(1L)
            if (years == 1L) "1 year ago" else "$years years ago"
        }
    }
}
