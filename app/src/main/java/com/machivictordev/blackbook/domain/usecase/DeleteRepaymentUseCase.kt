package com.machivictordev.blackbook.domain.usecase

import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.repository.DebtRepository

class DeleteRepaymentUseCase(private val repository: DebtRepository) {
    suspend operator fun invoke(repaymentId: Long): DebtMutationResult = repository.deleteRepayment(repaymentId)
}
