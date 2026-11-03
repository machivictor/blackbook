package com.machivictordev.blackbook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.machivictordev.blackbook.ui.details.DebtDetailsScreen
import com.machivictordev.blackbook.ui.details.DebtDetailsViewModel
import com.machivictordev.blackbook.ui.main.MainContainerScreen
import com.machivictordev.blackbook.ui.onboarding.CurrencySelectionScreen
import com.machivictordev.blackbook.ui.onboarding.OnboardingScreen
import com.machivictordev.blackbook.ui.settings.SettingsScreen
import com.machivictordev.blackbook.ui.util.LocalCurrency

private object Destinations {
    const val ONBOARDING = "onboarding"
    const val CURRENCY_SELECTION = "currency_selection"
    const val MAIN = "main"
    const val SETTINGS = "settings"
    const val DEBTOR_DETAILS = "debtor/{debtorId}"
    fun details(debtorId: Long) = "debtor/$debtorId"
}

@Composable
fun BlackBookNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val hasSeenOnboarding = remember { container.onboardingPreferences.hasSeenOnboarding() }
    val startDestination = if (hasSeenOnboarding) Destinations.MAIN else Destinations.ONBOARDING
    val currentCurrency by container.currencyPreferences.selectedCurrency.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalCurrency provides currentCurrency) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Destinations.ONBOARDING) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Destinations.CURRENCY_SELECTION)
                    }
                )
            }

            composable(Destinations.CURRENCY_SELECTION) {
                CurrencySelectionScreen(
                    currencyPreferences = container.currencyPreferences,
                    onFinish = {
                        container.onboardingPreferences.setSeenOnboarding(true)
                        navController.navigate(Destinations.MAIN) {
                            popUpTo(Destinations.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Destinations.MAIN) {
                MainContainerScreen(
                    container = container,
                    onOpenDebtor = { id -> navController.navigate(Destinations.details(id)) },
                    onOpenSettings = { navController.navigate(Destinations.SETTINGS) },
                )
            }

            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    currencyPreferences = container.currencyPreferences,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Destinations.DEBTOR_DETAILS,
                arguments = listOf(navArgument("debtorId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val debtorId = requireNotNull(backStackEntry.arguments).getLong("debtorId")
                val viewModel: DebtDetailsViewModel = viewModel(
                    factory = DebtDetailsViewModel.Factory(
                        debtorId = debtorId,
                        observeDetails = container.observeDebtorDetails,
                        addDebtUseCase = container.addDebt,
                        recordRepaymentUseCase = container.recordRepayment,
                        settleDebtorUseCase = container.settleDebtor,
                        updateDebtorNameUseCase = container.updateDebtorName,
                        deleteDebtorUseCase = container.deleteDebtor,
                        deleteDebtUseCase = container.deleteDebt,
                        deleteRepaymentUseCase = container.deleteRepayment,
                    ),
                )
                DebtDetailsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
