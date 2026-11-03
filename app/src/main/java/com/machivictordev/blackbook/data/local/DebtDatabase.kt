package com.machivictordev.blackbook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.machivictordev.blackbook.data.local.dao.DebtDao
import com.machivictordev.blackbook.data.local.dao.DebtorDao
import com.machivictordev.blackbook.data.local.dao.RepaymentDao
import com.machivictordev.blackbook.data.local.entity.DebtEntity
import com.machivictordev.blackbook.data.local.entity.DebtorEntity
import com.machivictordev.blackbook.data.local.entity.RepaymentEntity

@Database(
    entities = [DebtorEntity::class, DebtEntity::class, RepaymentEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class DebtDatabase : RoomDatabase() {
    abstract fun debtorDao(): DebtorDao
    abstract fun debtDao(): DebtDao
    abstract fun repaymentDao(): RepaymentDao
}
