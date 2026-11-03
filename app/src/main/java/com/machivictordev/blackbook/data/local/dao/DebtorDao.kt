package com.machivictordev.blackbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.machivictordev.blackbook.data.local.entity.DebtorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtorDao {
    @Query("""
        SELECT
            d.id AS debtorId,
            d.debtor_name AS name,
            d.created_at AS createdAtMillis,
            COALESCE((SELECT SUM(x.amount_minor) FROM debts x WHERE x.debtor_id = d.id), 0) AS totalDebtMinor,
            COALESCE((SELECT SUM(p.amount_minor) FROM debt_repayments p WHERE p.debtor_id = d.id), 0) AS totalRepaidMinor,
            MAX(
                d.created_at,
                COALESCE((SELECT MAX(x.timestamp_ms) FROM debts x WHERE x.debtor_id = d.id), d.created_at),
                COALESCE((SELECT MAX(p.timestamp_ms) FROM debt_repayments p WHERE p.debtor_id = d.id), d.created_at)
            ) AS lastActivityAtMillis
        FROM debtors d
        ORDER BY lastActivityAtMillis DESC, d.id DESC
    """)
    fun observeAccounts(): Flow<List<DebtorAccountRow>>

    @Query("SELECT * FROM debtors WHERE id = :debtorId LIMIT 1")
    fun observeById(debtorId: Long): Flow<DebtorEntity?>

    @Query("SELECT * FROM debtors ORDER BY debtor_name ASC")
    fun observeAll(): Flow<List<DebtorEntity>>

    @Query("SELECT * FROM debtors WHERE id = :debtorId LIMIT 1")
    suspend fun findById(debtorId: Long): DebtorEntity?

    @Query("SELECT * FROM debtors WHERE debtor_name LIKE :name COLLATE NOCASE LIMIT 1")
    suspend fun findByName(name: String): DebtorEntity?

    @Query("UPDATE debtors SET debtor_name = :newName WHERE id = :debtorId")
    suspend fun updateName(debtorId: Long, newName: String)

    @Query("DELETE FROM debtors WHERE id = :debtorId")
    suspend fun deleteById(debtorId: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(debtor: DebtorEntity): Long
}
