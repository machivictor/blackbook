package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class DeleteDebtUseCase(private val repository: DebtRepository) {
    suspend operator fun invoke(debtId: Long): DebtMutationResult = repository.deleteDebt(debtId)
}
