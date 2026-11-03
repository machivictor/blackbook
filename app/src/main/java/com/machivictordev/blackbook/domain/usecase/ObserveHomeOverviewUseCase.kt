package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.model.HomeOverview
import com.machivictordev.blackbook.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveHomeOverviewUseCase(
    private val repository: DebtRepository,
) {
    operator fun invoke(): Flow<HomeOverview> = repository.observeDebtorAccounts().map { accounts ->
        val active = accounts.filter { it.balanceMinor > 0L }
        val settled = accounts.filter { it.balanceMinor == 0L }
        HomeOverview(
            totalOutstandingMinor = active.sumOf { it.balanceMinor },
            totalDueMinor = accounts.sumOf { it.totalDebtMinor },
            totalCollectedMinor = accounts.sumOf { it.totalRepaidMinor },
            activeDebtors = active,
            settledDebtors = settled,
        )
    }
}
