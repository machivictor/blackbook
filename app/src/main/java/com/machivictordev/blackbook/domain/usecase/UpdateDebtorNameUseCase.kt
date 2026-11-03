package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class UpdateDebtorNameUseCase(private val repository: DebtRepository) {
    suspend operator fun invoke(debtorId: Long, newName: String): DebtMutationResult =
        if (newName.isBlank()) DebtMutationResult.InvalidAmount
        else repository.updateDebtorName(debtorId, newName)
}
