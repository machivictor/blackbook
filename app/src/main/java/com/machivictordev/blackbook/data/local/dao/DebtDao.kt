package com.machivictordev.blackbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.machivictordev.blackbook.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts WHERE debtor_id = :debtorId ORDER BY timestamp_ms DESC, id DESC")
    fun observeForDebtor(debtorId: Long): Flow<List<DebtEntity>>

    @Query("SELECT COALESCE(SUM(amount_minor), 0) FROM debts WHERE debtor_id = :debtorId")
    suspend fun totalAmountMinor(debtorId: Long): Long

    @Query("DELETE FROM debts WHERE id = :debtId")
    suspend fun deleteById(debtId: Long)

    @Query("DELETE FROM debts WHERE debtor_id = :debtorId")
    suspend fun deleteForDebtor(debtorId: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(debt: DebtEntity): Long
}
