package com.machivictordev.blackbook.ui.util

import com.machivictordev.blackbook.domain.model.Currency
import com.machivictordev.blackbook.domain.model.CurrencyRegion
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class PresentationFormattersTest {

    private val zoneId = ZoneId.systemDefault()
    private val nowDateTime = LocalDateTime.of(2026, 10, 27, 12, 0)
    private val nowMillis = nowDateTime.atZone(zoneId).toInstant().toEpochMilli()

    @Test
    fun formatMoney_defaultSymbol() {
        assertEquals("Ksh. 1,000", formatMoney(100000L))
        assertEquals("Ksh. 1,000.50", formatMoney(100050L))
        assertEquals("Ksh. 0", formatMoney(0L))
    }

    @Test
    fun formatMoney_customSymbols() {
        assertEquals("$1,000", formatMoney(100000L, "$"))
        assertEquals("€1,000.75", formatMoney(100075L, "€"))
        assertEquals("₦5,000", formatMoney(500000L, "₦"))
        assertEquals("USh 10,000", formatMoney(1000000L, "USh "))
    }

    @Test
    fun formatMoney_currencyObject() {
        val usd = Currency("USD", "US Dollar", "$", CurrencyRegion.AMERICA)
        val eur = Currency("EUR", "Euro", "€", CurrencyRegion.EUROPE)
        assertEquals("$250", formatMoney(25000L, usd))
        assertEquals("€1,200.50", formatMoney(120050L, eur))
    }

    @Test
    fun formatRelativeDate_today() {
        val sameDayMillis = nowDateTime.minusHours(2).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("Today", formatRelativeDate(sameDayMillis, nowMillis))
    }

    @Test
    fun formatRelativeDate_yesterday() {
        val yesterdayMillis = nowDateTime.minusDays(1).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("Yesterday", formatRelativeDate(yesterdayMillis, nowMillis))
    }

    @Test
    fun formatRelativeDate_daysAgo() {
        val threeDaysAgoMillis = nowDateTime.minusDays(3).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("3 days ago", formatRelativeDate(threeDaysAgoMillis, nowMillis))
    }

    @Test
    fun formatRelativeDate_weeksAgo() {
        val oneWeekAgoMillis = nowDateTime.minusDays(7).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("1 week ago", formatRelativeDate(oneWeekAgoMillis, nowMillis))

        val twoWeeksAgoMillis = nowDateTime.minusDays(14).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("2 weeks ago", formatRelativeDate(twoWeeksAgoMillis, nowMillis))
    }

    @Test
    fun formatRelativeDate_monthsAgo() {
        val oneMonthAgoMillis = nowDateTime.minusDays(35).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("1 month ago", formatRelativeDate(oneMonthAgoMillis, nowMillis))

        val threeMonthsAgoMillis = nowDateTime.minusMonths(3).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("3 months ago", formatRelativeDate(threeMonthsAgoMillis, nowMillis))
    }

    @Test
    fun formatRelativeDate_yearsAgo() {
        val oneYearAgoMillis = nowDateTime.minusYears(1).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("1 year ago", formatRelativeDate(oneYearAgoMillis, nowMillis))

        val twoYearsAgoMillis = nowDateTime.minusYears(2).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals("2 years ago", formatRelativeDate(twoYearsAgoMillis, nowMillis))
    }
}
