package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.model.Debtor
import com.machivictordev.blackbook.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow

class ObserveDebtorsUseCase(private val repository: DebtRepository) {
    operator fun invoke(): Flow<List<Debtor>> = repository.observeDebtors()
}
