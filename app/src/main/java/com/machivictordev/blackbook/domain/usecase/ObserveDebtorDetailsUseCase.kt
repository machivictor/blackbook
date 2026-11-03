package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.model.DebtorDetails
import com.machivictordev.blackbook.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveDebtorDetailsUseCase(
    private val repository: DebtRepository,
) {
    operator fun invoke(debtorId: Long): Flow<DebtorDetails?> = combine(
        repository.observeDebtor(debtorId),
        repository.observeDebts(debtorId),
        repository.observeRepayments(debtorId),
    ) { debtor, debts, repayments ->
        debtor?.let { DebtorDetails(it, debts, repayments) }
    }
}
