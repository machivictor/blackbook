package com.machivictordev.blackbook.domain.model

enum class CurrencyRegion(val displayName: String) {
    AFRICA("Africa"),
    AMERICA("America"),
    ASIA("Asia"),
    EUROPE("Europe"),
}

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val region: CurrencyRegion,
    val flagEmoji: String = "",
) {
    companion object {
        val DEFAULT = Currency("KES", "Kenyan Shilling", "Ksh.", CurrencyRegion.AFRICA, "🇰🇪")

        val ALL_CURRENCIES = listOf(
            // Africa
            Currency("KES", "Kenyan Shilling", "KSh.", CurrencyRegion.AFRICA, "🇰🇪"),
            Currency("ETB", "Ethiopian Birr", "Br", CurrencyRegion.AFRICA, "🇪🇹"),
            Currency("NGN", "Nigerian Naira", "₦", CurrencyRegion.AFRICA, "🇳🇬"),
            Currency("ZAR", "South African Rand", "R", CurrencyRegion.AFRICA, "🇿🇦"),
            Currency("EGP", "Egyptian Pound", "E£", CurrencyRegion.AFRICA, "🇪🇬"),
            Currency("GHS", "Ghanaian Cedi", "GH₵", CurrencyRegion.AFRICA, "🇬🇭"),
            Currency("UGX", "Ugandan Shilling", "USh", CurrencyRegion.AFRICA, "🇺🇬"),
            Currency("TZS", "Tanzanian Shilling", "TSh", CurrencyRegion.AFRICA, "🇹🇿"),
            Currency("RWF", "Rwandan Franc", "FRw", CurrencyRegion.AFRICA, "🇷🇼"),
            Currency("MAD", "Moroccan Dirham", "MAD", CurrencyRegion.AFRICA, "🇲🇦"),

            // America
            Currency("USD", "US Dollar", "$", CurrencyRegion.AMERICA, "🇺🇸"),
            Currency("CAD", "Canadian Dollar", "CA$", CurrencyRegion.AMERICA, "🇨🇦"),
            Currency("BRL", "Brazilian Real", "R$", CurrencyRegion.AMERICA, "🇧🇷"),
            Currency("MXN", "Mexican Peso", "Mex$", CurrencyRegion.AMERICA, "🇲🇽"),
            Currency("ARS", "Argentine Peso", "ARS$", CurrencyRegion.AMERICA, "🇦🇷"),
            Currency("COP", "Colombian Peso", "COL$", CurrencyRegion.AMERICA, "🇨🇴"),

            // Asia
            Currency("INR", "Indian Rupee", "₹", CurrencyRegion.ASIA, "🇮🇳"),
            Currency("JPY", "Japanese Yen", "¥", CurrencyRegion.ASIA, "🇯🇵"),
            Currency("CNY", "Chinese Yuan", "¥", CurrencyRegion.ASIA, "🇨🇳"),
            Currency("AED", "UAE Dirham", "AED", CurrencyRegion.ASIA, "🇦🇪"),
            Currency("SAR", "Saudi Riyal", "SAR", CurrencyRegion.ASIA, "🇸🇦"),
            Currency("SGD", "Singapore Dollar", "S$", CurrencyRegion.ASIA, "🇸🇬"),
            Currency("PKR", "Pakistani Rupee", "Rs", CurrencyRegion.ASIA, "🇵🇰"),
            Currency("IDR", "Indonesian Rupiah", "Rp", CurrencyRegion.ASIA, "🇮🇩"),
            Currency("PHP", "Philippine Peso", "₱", CurrencyRegion.ASIA, "🇵🇭"),

            // Europe
            Currency("EUR", "Euro", "€", CurrencyRegion.EUROPE, "🇪🇺"),
            Currency("GBP", "British Pound", "£", CurrencyRegion.EUROPE, "🇬🇧"),
            Currency("CHF", "Swiss Franc", "CHF", CurrencyRegion.EUROPE, "🇨🇭"),
            Currency("SEK", "Swedish Krona", "kr", CurrencyRegion.EUROPE, "🇸🇪"),
            Currency("NOK", "Norwegian Krone", "kr", CurrencyRegion.EUROPE, "🇳🇴"),
            Currency("PLN", "Polish Zloty", "zł", CurrencyRegion.EUROPE, "🇵🇱"),
            Currency("TRY", "Turkish Lira", "₺", CurrencyRegion.EUROPE, "🇹🇷"),
        )

        fun findByCode(code: String): Currency {
            return ALL_CURRENCIES.find { it.code.equals(code, ignoreCase = true) } ?: DEFAULT
        }
    }
}
