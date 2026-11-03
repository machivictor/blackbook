package com.machivictordev.blackbook.data.repository

import androidx.room.withTransaction
import com.machivictordev.blackbook.data.local.DebtDatabase
import com.machivictordev.blackbook.data.local.dao.DebtorAccountRow
import com.machivictordev.blackbook.data.local.entity.DebtEntity
import com.machivictordev.blackbook.data.local.entity.DebtorEntity
import com.machivictordev.blackbook.data.local.entity.RepaymentEntity
import com.machivictordev.blackbook.domain.model.DebtEntry
import com.machivictordev.blackbook.domain.model.Debtor
import com.machivictordev.blackbook.domain.model.DebtorAccount
import com.machivictordev.blackbook.domain.model.RepaymentEntry
import com.machivictordev.blackbook.domain.repository.CreateDebtorResult
import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.repository.DebtRepository
import java.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDebtRepository(
    private val database: DebtDatabase,
    private val clock: Clock = Clock.systemUTC(),
) : DebtRepository {
    private val debtorDao = database.debtorDao()
    private val debtDao = database.debtDao()
    private val repaymentDao = database.repaymentDao()

    override fun observeDebtorAccounts(): Flow<List<DebtorAccount>> =
        debtorDao.observeAccounts().map { rows -> rows.map { it.toDomain() } }

    override fun observeDebtors(): Flow<List<Debtor>> =
        debtorDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeDebtor(debtorId: Long): Flow<Debtor?> =
        debtorDao.observeById(debtorId).map { entity -> entity?.toDomain() }

    override fun observeDebts(debtorId: Long): Flow<List<DebtEntry>> =
        debtDao.observeForDebtor(debtorId).map { debts ->
            debts.map { DebtEntry(it.id, it.amountMinor, it.timestampMillis, it.note) }
        }

    override fun observeRepayments(debtorId: Long): Flow<List<RepaymentEntry>> =
        repaymentDao.observeForDebtor(debtorId).map { repayments ->
            repayments.map { RepaymentEntry(it.id, it.amountMinor, it.timestampMillis, it.note) }
        }

    override suspend fun getOrCreateDebtorAndAddDebt(name: String, amountMinor: Long, note: String): Long =
        database.withTransaction {
            require(name.isNotBlank()) { "Debtor name must not be blank." }
            require(amountMinor > 0L) { "Amount must be positive." }

            val trimmedName = name.trim()
            val existing = debtorDao.findByName(trimmedName)
            val debtorId = existing?.id ?: debtorDao.insert(
                DebtorEntity(name = trimmedName, createdAtMillis = clock.millis())
            )
            debtDao.insert(
                DebtEntity(
                    debtorId = debtorId,
                    amountMinor = amountMinor,
                    timestampMillis = clock.millis(),
                    note = note.trim(),
                ),
            )
            debtorId
        }

    override suspend fun createDebtorOnly(name: String): CreateDebtorResult =
        database.withTransaction {
            val trimmedName = name.trim()
            if (trimmedName.isBlank()) return@withTransaction CreateDebtorResult.InvalidName
            val existing = debtorDao.findByName(trimmedName)
            if (existing != null) {
                return@withTransaction CreateDebtorResult.AlreadyExists
            }
            val id = debtorDao.insert(
                DebtorEntity(name = trimmedName, createdAtMillis = clock.millis())
            )
            CreateDebtorResult.Created(id)
        }

    override suspend fun addDebt(debtorId: Long, amountMinor: Long, note: String): DebtMutationResult =
        database.withTransaction {
            if (amountMinor <= 0L) return@withTransaction DebtMutationResult.InvalidAmount
            if (debtorDao.findById(debtorId) == null) {
                return@withTransaction DebtMutationResult.DebtorNotFound
            }
            debtDao.insert(
                DebtEntity(
                    debtorId = debtorId,
                    amountMinor = amountMinor,
                    timestampMillis = clock.millis(),
                    note = note.trim(),
                ),
            )
            DebtMutationResult.Success(amountMinor)
        }

    override suspend fun recordRepayment(debtorId: Long, amountMinor: Long, note: String): DebtMutationResult =
        database.withTransaction {
            if (amountMinor <= 0L) return@withTransaction DebtMutationResult.InvalidAmount
            if (debtorDao.findById(debtorId) == null) {
                return@withTransaction DebtMutationResult.DebtorNotFound
            }

            val balance = currentBalanceMinor(debtorId)
            if (balance <= 0L) return@withTransaction DebtMutationResult.NoOutstandingBalance
            if (amountMinor > balance) {
                return@withTransaction DebtMutationResult.AmountExceedsBalance(balance)
            }

            repaymentDao.insert(
                RepaymentEntity(
                    debtorId = debtorId,
                    amountMinor = amountMinor,
                    timestampMillis = clock.millis(),
                    note = note.trim(),
                ),
            )
            DebtMutationResult.Success(amountMinor)
        }

    override suspend fun updateDebtorName(debtorId: Long, newName: String): DebtMutationResult =
        database.withTransaction {
            val cleanName = newName.trim()
            if (cleanName.isBlank()) return@withTransaction DebtMutationResult.InvalidAmount
            if (debtorDao.findById(debtorId) == null) {
                return@withTransaction DebtMutationResult.DebtorNotFound
            }
            debtorDao.updateName(debtorId, cleanName)
            DebtMutationResult.Success(0L)
        }

    override suspend fun settleDebtor(debtorId: Long): DebtMutationResult = database.withTransaction {
        if (debtorDao.findById(debtorId) == null) {
            return@withTransaction DebtMutationResult.DebtorNotFound
        }

        val exactRemainingBalance = currentBalanceMinor(debtorId)
        if (exactRemainingBalance <= 0L) {
            return@withTransaction DebtMutationResult.NoOutstandingBalance
        }

        repaymentDao.insert(
            RepaymentEntity(
                debtorId = debtorId,
                amountMinor = exactRemainingBalance,
                timestampMillis = clock.millis(),
                note = "Settle balance",
            ),
        )
        DebtMutationResult.Success(exactRemainingBalance)
    }

    override suspend fun deleteDebtor(debtorId: Long): DebtMutationResult = database.withTransaction {
        if (debtorDao.findById(debtorId) == null) {
            return@withTransaction DebtMutationResult.DebtorNotFound
        }
        debtDao.deleteForDebtor(debtorId)
        repaymentDao.deleteForDebtor(debtorId)
        debtorDao.deleteById(debtorId)
        DebtMutationResult.Success(0L)
    }

    override suspend fun deleteDebt(debtId: Long): DebtMutationResult = database.withTransaction {
        debtDao.deleteById(debtId)
        DebtMutationResult.Success(0L)
    }

    override suspend fun deleteRepayment(repaymentId: Long): DebtMutationResult = database.withTransaction {
        repaymentDao.deleteById(repaymentId)
        DebtMutationResult.Success(0L)
    }

    private suspend fun currentBalanceMinor(debtorId: Long): Long =
        debtDao.totalAmountMinor(debtorId) - repaymentDao.totalAmountMinor(debtorId)

    private fun DebtorEntity.toDomain() = Debtor(
        id = id,
        name = name,
        createdAtMillis = createdAtMillis,
    )

    private fun DebtorAccountRow.toDomain() = DebtorAccount(
        id = debtorId,
        name = name,
        createdAtMillis = createdAtMillis,
        totalDebtMinor = totalDebtMinor,
        totalRepaidMinor = totalRepaidMinor,
        lastActivityAtMillis = lastActivityAtMillis,
    )
}
