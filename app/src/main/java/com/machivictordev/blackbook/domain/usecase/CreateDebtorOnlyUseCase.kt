package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.CreateDebtorResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class CreateDebtorOnlyUseCase(
    private val repository: DebtRepository,
) {
    suspend operator fun invoke(name: String): CreateDebtorResult {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return CreateDebtorResult.InvalidName
        return repository.createDebtorOnly(cleanName)
    }
}
