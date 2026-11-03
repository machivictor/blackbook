package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.CreateDebtorResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class CreateDebtorUseCase(
    private val repository: DebtRepository,
) {
    suspend operator fun invoke(name: String, initialAmountMinor: Long, note: String = ""): CreateDebtorResult {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return CreateDebtorResult.InvalidName
        if (initialAmountMinor <= 0L) return CreateDebtorResult.InvalidAmount
        val id = repository.getOrCreateDebtorAndAddDebt(cleanName, initialAmountMinor, note)
        return CreateDebtorResult.Created(id)
    }
}
