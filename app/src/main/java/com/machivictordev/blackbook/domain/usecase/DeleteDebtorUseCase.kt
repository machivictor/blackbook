package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class DeleteDebtorUseCase(private val repository: DebtRepository) {
    suspend operator fun invoke(debtorId: Long): DebtMutationResult = repository.deleteDebtor(debtorId)
}
