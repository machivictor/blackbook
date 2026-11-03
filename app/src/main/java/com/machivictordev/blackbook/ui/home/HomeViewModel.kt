package com.machivictordev.blackbook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.machivictordev.blackbook.domain.model.HomeOverview
import com.machivictordev.blackbook.domain.repository.CreateDebtorResult
import com.machivictordev.blackbook.domain.usecase.CreateDebtorUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveHomeOverviewUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeTab {
    OUTSTANDING,
    SETTLED,
}

enum class HomeSortOption(val label: String) {
    NEWEST_DATE("Newest date"),
    OLDEST_DATE("Oldest date"),
    HIGHEST_AMOUNT("Highest amount"),
    LOWEST_AMOUNT("Lowest amount"),
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val overview: HomeOverview = HomeOverview(),
    val selectedTab: HomeTab = HomeTab.OUTSTANDING,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val sortOption: HomeSortOption = HomeSortOption.NEWEST_DATE,
)

class HomeViewModel(
    private val observeHomeOverview: ObserveHomeOverviewUseCase,
    private val createDebtor: CreateDebtorUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    private val _scrollToTop = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToTop = _scrollToTop.asSharedFlow()

    init {
        viewModelScope.launch {
            observeHomeOverview()
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                    _messages.emit("Could not load debt records. Please reopen the app.")
                }
                .collect { overview ->
                    _uiState.update { it.copy(isLoading = false, overview = overview) }
                }
        }
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSortOption(option: HomeSortOption) {
        _uiState.update { it.copy(sortOption = option) }
    }

    fun toggleSearch() {
        _uiState.update {
            val nextState = !it.isSearchActive
            it.copy(isSearchActive = nextState, searchQuery = if (!nextState) "" else it.searchQuery)
        }
    }

    fun addDebtor(name: String, initialAmountMinor: Long, note: String) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                when (createDebtor(name, initialAmountMinor, note)) {
                    is CreateDebtorResult.Created -> {
                        _messages.emit("Debt added for ${name.trim()}.")
                        _scrollToTop.emit(Unit)
                    }
                    CreateDebtorResult.InvalidName -> _messages.emit("Enter the person's name.")
                    CreateDebtorResult.InvalidAmount -> _messages.emit("Enter an amount greater than zero.")
                    CreateDebtorResult.AlreadyExists -> _messages.emit("A debtor with this name already exists.")
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _messages.emit("Could not save this debt. Please try again.")
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    class Factory(
        private val observeHomeOverview: ObserveHomeOverviewUseCase,
        private val createDebtor: CreateDebtorUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(observeHomeOverview, createDebtor) as T
        }
    }
}
