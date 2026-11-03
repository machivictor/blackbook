package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class AddDebtUseCase(private val repository: DebtRepository) {
    suspend operator fun invoke(debtorId: Long, amountMinor: Long, note: String = ""): DebtMutationResult =
        if (amountMinor <= 0L) DebtMutationResult.InvalidAmount
        else repository.addDebt(debtorId, amountMinor, note)
}
