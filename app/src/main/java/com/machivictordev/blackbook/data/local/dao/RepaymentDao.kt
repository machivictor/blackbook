package com.machivictordev.blackbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.machivictordev.blackbook.data.local.entity.RepaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepaymentDao {
    @Query("SELECT * FROM debt_repayments WHERE debtor_id = :debtorId ORDER BY timestamp_ms DESC, id DESC")
    fun observeForDebtor(debtorId: Long): Flow<List<RepaymentEntity>>

    @Query("SELECT COALESCE(SUM(amount_minor), 0) FROM debt_repayments WHERE debtor_id = :debtorId")
    suspend fun totalAmountMinor(debtorId: Long): Long

    @Query("DELETE FROM debt_repayments WHERE id = :repaymentId")
    suspend fun deleteById(repaymentId: Long)

    @Query("DELETE FROM debt_repayments WHERE debtor_id = :debtorId")
    suspend fun deleteForDebtor(debtorId: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(repayment: RepaymentEntity): Long
}
