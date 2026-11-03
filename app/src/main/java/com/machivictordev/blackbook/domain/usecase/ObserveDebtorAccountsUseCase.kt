package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.model.DebtorAccount
import com.machivictordev.blackbook.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow

class ObserveDebtorAccountsUseCase(private val repository: DebtRepository) {
    operator fun invoke(): Flow<List<DebtorAccount>> = repository.observeDebtorAccounts()
}
