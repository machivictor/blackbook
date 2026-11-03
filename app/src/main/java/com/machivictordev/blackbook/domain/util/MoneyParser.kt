package com.machivictordev.blackbook.domain.util

import java.math.BigDecimal
import java.math.RoundingMode

/** Converts a positive Ksh amount with at most two decimal places into exact minor units. */
object MoneyParser {
    private val amountPattern = Regex("""^\d+(?:\.\d{1,2})?$""")

    fun parseMinorUnits(input: String): Long? {
        val value = input.trim()
        if (!amountPattern.matches(value)) return null

        return try {
            BigDecimal(value)
                .setScale(2, RoundingMode.UNNECESSARY)
                .movePointRight(2)
                .longValueExact()
                .takeIf { it > 0L }
        } catch (_: ArithmeticException) {
            null
        } catch (_: NumberFormatException) {
            null
        }
    }
}
