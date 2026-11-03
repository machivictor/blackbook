package com.machivictordev.blackbook.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.machivictordev.blackbook.AppContainer
import com.machivictordev.blackbook.ui.home.HomeScreen
import com.machivictordev.blackbook.ui.home.HomeViewModel
import com.machivictordev.blackbook.ui.people.PeopleScreen
import com.machivictordev.blackbook.ui.people.PeopleViewModel

sealed class MainTab(
    val route: String,
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    data object Home : MainTab("home_tab", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object People : MainTab("people_tab", "People", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainerScreen(
    container: AppContainer,
    onOpenDebtor: (Long) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val tabs = listOf(MainTab.Home, MainTab.People)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            observeHomeOverview = container.observeHomeOverview,
            createDebtor = container.createDebtor,
        ),
    )

    val peopleViewModel: PeopleViewModel = viewModel(
        factory = PeopleViewModel.Factory(
            observeDebtorAccounts = container.observeDebtorAccounts,
            createDebtorOnly = container.createDebtorOnly,
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column() {

                        Text(
                            text = "Black Book",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Text(
                            text = "Your digital debt book",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.8f
                            )
                        )
                    }
                },
                actions = {
                    Row() {
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ),
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                tabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                            )
                        },
                        label = { Text(tab.title) },
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MainTab.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(MainTab.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    observeDebtorsForSuggestions = {
                        val debtors by container.observeDebtors().collectAsStateWithLifecycle(initialValue = emptyList())
                        debtors
                    },
                    onOpenDebtor = onOpenDebtor
                )
            }
            composable(MainTab.People.route) {
                PeopleScreen(
                    viewModel = peopleViewModel,
                    observeDebtorsForSuggestions = {
                        val debtors by container.observeDebtors().collectAsStateWithLifecycle(initialValue = emptyList())
                        debtors
                    },
                    onOpenDebtor = onOpenDebtor
                )
            }
        }
    }
}
