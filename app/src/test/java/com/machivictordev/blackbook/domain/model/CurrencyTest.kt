package com.machivictordev.blackbook.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyTest {

    @Test
    fun defaultCurrency_isKes() {
        assertEquals("KES", Currency.DEFAULT.code)
        assertEquals("Kenyan Shilling", Currency.DEFAULT.name)
        assertEquals("Ksh.", Currency.DEFAULT.symbol)
        assertEquals(CurrencyRegion.AFRICA, Currency.DEFAULT.region)
    }

    @Test
    fun findByCode_returnsCorrectCurrency() {
        val usd = Currency.findByCode("USD")
        assertEquals("USD", usd.code)
        assertEquals("$", usd.symbol)
        assertEquals(CurrencyRegion.AMERICA, usd.region)

        val eur = Currency.findByCode("eur") // case insensitive
        assertEquals("EUR", eur.code)
        assertEquals("€", eur.symbol)
        assertEquals(CurrencyRegion.EUROPE, eur.region)

        val ngn = Currency.findByCode("NGN")
        assertEquals("NGN", ngn.code)
        assertEquals("₦", ngn.symbol)
        assertEquals(CurrencyRegion.AFRICA, ngn.region)

        val inr = Currency.findByCode("INR")
        assertEquals("INR", inr.code)
        assertEquals("₹", inr.symbol)
        assertEquals(CurrencyRegion.ASIA, inr.region)
    }

    @Test
    fun findByCode_unknownCode_returnsDefault() {
        val unknown = Currency.findByCode("XYZ")
        assertEquals(Currency.DEFAULT, unknown)
    }

    @Test
    fun allCurrencies_containsRegions() {
        val africaCurrencies = Currency.ALL_CURRENCIES.filter { it.region == CurrencyRegion.AFRICA }
        val americaCurrencies = Currency.ALL_CURRENCIES.filter { it.region == CurrencyRegion.AMERICA }
        val asiaCurrencies = Currency.ALL_CURRENCIES.filter { it.region == CurrencyRegion.ASIA }
        val europeCurrencies = Currency.ALL_CURRENCIES.filter { it.region == CurrencyRegion.EUROPE }

        assert(africaCurrencies.isNotEmpty())
        assert(americaCurrencies.isNotEmpty())
        assert(asiaCurrencies.isNotEmpty())
        assert(europeCurrencies.isNotEmpty())
    }
}
