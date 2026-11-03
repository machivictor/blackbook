package com.machivictordev.blackbook.domain.model

data class Debtor(
    val id: Long,
    val name: String,
    val createdAtMillis: Long,
)

data class DebtorAccount(
    val id: Long,
    val name: String,
    val createdAtMillis: Long,
    val totalDebtMinor: Long,
    val totalRepaidMinor: Long,
    val lastActivityAtMillis: Long,
) {
    val balanceMinor: Long
        get() = (totalDebtMinor - totalRepaidMinor).coerceAtLeast(0L)
}

data class DebtEntry(
    val id: Long,
    val amountMinor: Long,
    val timestampMillis: Long,
    val note: String,
)

data class RepaymentEntry(
    val id: Long,
    val amountMinor: Long,
    val timestampMillis: Long,
    val note: String,
)

data class DebtorDetails(
    val debtor: Debtor,
    val debts: List<DebtEntry>,
    val repayments: List<RepaymentEntry>,
) {
    val totalDebtMinor: Long
        get() = debts.sumOf(DebtEntry::amountMinor)

    val totalRepaidMinor: Long
        get() = repayments.sumOf(RepaymentEntry::amountMinor)

    val balanceMinor: Long
        get() = (totalDebtMinor - totalRepaidMinor).coerceAtLeast(0L)

    val isSettled: Boolean
        get() = balanceMinor == 0L

    val lastActivityAtMillis: Long
        get() {
            val maxDebt = debts.maxOfOrNull { it.timestampMillis } ?: 0L
            val maxRepayment = repayments.maxOfOrNull { it.timestampMillis } ?: 0L
            val maxEntry = maxOf(maxDebt, maxRepayment)
            return if (maxEntry > 0L) maxEntry else debtor.createdAtMillis
        }
}

data class HomeOverview(
    val totalOutstandingMinor: Long = 0L,
    val totalDueMinor: Long = 0L,
    val totalCollectedMinor: Long = 0L,
    val activeDebtors: List<DebtorAccount> = emptyList(),
    val settledDebtors: List<DebtorAccount> = emptyList(),
)
