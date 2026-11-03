package com.machivictordev.blackbook.data.local.dao

import androidx.room.ColumnInfo

/** Projection returned by the reactive, aggregate home-screen query. */
data class DebtorAccountRow(
    @ColumnInfo(name = "debtorId") val debtorId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "createdAtMillis") val createdAtMillis: Long,
    @ColumnInfo(name = "totalDebtMinor") val totalDebtMinor: Long,
    @ColumnInfo(name = "totalRepaidMinor") val totalRepaidMinor: Long,
    @ColumnInfo(name = "lastActivityAtMillis") val lastActivityAtMillis: Long,
)
