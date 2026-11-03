package com.machivictordev.blackbook.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debtors")
data class DebtorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "debtor_name")
    val name: String,
    @ColumnInfo(name = "created_at")
    val createdAtMillis: Long,
)
