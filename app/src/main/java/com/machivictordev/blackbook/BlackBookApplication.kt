package com.machivictordev.blackbook

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.machivictordev.blackbook.data.local.DebtDatabase
import com.machivictordev.blackbook.data.preferences.CurrencyPreferences
import com.machivictordev.blackbook.data.preferences.OnboardingPreferences
import com.machivictordev.blackbook.data.repository.RoomDebtRepository
import com.machivictordev.blackbook.domain.repository.DebtRepository
import com.machivictordev.blackbook.domain.usecase.AddDebtUseCase
import com.machivictordev.blackbook.domain.usecase.CreateDebtorOnlyUseCase
import com.machivictordev.blackbook.domain.usecase.CreateDebtorUseCase
import com.machivictordev.blackbook.domain.usecase.DeleteDebtUseCase
import com.machivictordev.blackbook.domain.usecase.DeleteDebtorUseCase
import com.machivictordev.blackbook.domain.usecase.DeleteRepaymentUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveDebtorAccountsUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveDebtorDetailsUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveDebtorsUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveHomeOverviewUseCase
import com.machivictordev.blackbook.domain.usecase.RecordRepaymentUseCase
import com.machivictordev.blackbook.domain.usecase.SettleDebtorUseCase
import com.machivictordev.blackbook.domain.usecase.UpdateDebtorNameUseCase

class BlackBookApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}

class AppContainer(context: Context) {
    private val database: DebtDatabase = Room.databaseBuilder(
        context.applicationContext,
        DebtDatabase::class.java,
        "debt_ledger.db",
    ).fallbackToDestructiveMigration().build()

    private val repository: DebtRepository = RoomDebtRepository(database)

    val onboardingPreferences = OnboardingPreferences(context)
    val currencyPreferences = CurrencyPreferences(context)
    val observeHomeOverview = ObserveHomeOverviewUseCase(repository)
    val observeDebtorAccounts = ObserveDebtorAccountsUseCase(repository)
    val observeDebtors = ObserveDebtorsUseCase(repository)
    val createDebtor = CreateDebtorUseCase(repository)
    val createDebtorOnly = CreateDebtorOnlyUseCase(repository)
    val observeDebtorDetails = ObserveDebtorDetailsUseCase(repository)
    val addDebt = AddDebtUseCase(repository)
    val recordRepayment = RecordRepaymentUseCase(repository)
    val settleDebtor = SettleDebtorUseCase(repository)
    val updateDebtorName = UpdateDebtorNameUseCase(repository)
    val deleteDebtor = DeleteDebtorUseCase(repository)
    val deleteDebt = DeleteDebtUseCase(repository)
    val deleteRepayment = DeleteRepaymentUseCase(repository)
}
