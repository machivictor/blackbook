package com.machivictordev.blackbook.domain.repository

import com.machivictordev.blackbook.domain.model.DebtEntry
import com.machivictordev.blackbook.domain.model.Debtor
import com.machivictordev.blackbook.domain.model.DebtorAccount
import com.machivictordev.blackbook.domain.model.RepaymentEntry
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    fun observeDebtorAccounts(): Flow<List<DebtorAccount>>
    fun observeDebtors(): Flow<List<Debtor>>
    fun observeDebtor(debtorId: Long): Flow<Debtor?>
    fun observeDebts(debtorId: Long): Flow<List<DebtEntry>>
    fun observeRepayments(debtorId: Long): Flow<List<RepaymentEntry>>

    suspend fun getOrCreateDebtorAndAddDebt(name: String, amountMinor: Long, note: String): Long
    suspend fun createDebtorOnly(name: String): CreateDebtorResult
    suspend fun addDebt(debtorId: Long, amountMinor: Long, note: String): DebtMutationResult
    suspend fun recordRepayment(debtorId: Long, amountMinor: Long, note: String): DebtMutationResult
    suspend fun updateDebtorName(debtorId: Long, newName: String): DebtMutationResult
    suspend fun settleDebtor(debtorId: Long): DebtMutationResult
    suspend fun deleteDebtor(debtorId: Long): DebtMutationResult
    suspend fun deleteDebt(debtId: Long): DebtMutationResult
    suspend fun deleteRepayment(repaymentId: Long): DebtMutationResult
}

sealed interface DebtMutationResult {
    data class Success(val amountMinor: Long) : DebtMutationResult
    data object InvalidAmount : DebtMutationResult
    data object DebtorNotFound : DebtMutationResult
    data object NoOutstandingBalance : DebtMutationResult
    data class AmountExceedsBalance(val balanceMinor: Long) : DebtMutationResult
}

sealed interface CreateDebtorResult {
    data class Created(val debtorId: Long) : CreateDebtorResult
    data object InvalidName : CreateDebtorResult
    data object InvalidAmount : CreateDebtorResult
    data object AlreadyExists : CreateDebtorResult
}
