package com.machivictordev.blackbook.ui.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.machivictordev.blackbook.domain.model.DebtorAccount
import com.machivictordev.blackbook.domain.repository.CreateDebtorResult
import com.machivictordev.blackbook.domain.usecase.CreateDebtorOnlyUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveDebtorAccountsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PeopleUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val debtors: List<DebtorAccount> = emptyList(),
    val searchQuery: String = "",
)

class PeopleViewModel(
    observeDebtorAccounts: ObserveDebtorAccountsUseCase,
    private val createDebtorOnly: CreateDebtorOnlyUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
            observeDebtorAccounts()
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                    _messages.emit("Could not load people records.")
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, debtors = list) }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun addPerson(name: String) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                when (val result = createDebtorOnly(name)) {
                    is CreateDebtorResult.Created -> _messages.emit("${name.trim()} added.")
                    CreateDebtorResult.InvalidName -> _messages.emit("Enter the person's name.")
                    CreateDebtorResult.AlreadyExists -> _messages.emit("${name.trim()} already exists.")
                    else -> {}
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _messages.emit("Could not add person. Please try again.")
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    class Factory(
        private val observeDebtorAccounts: ObserveDebtorAccountsUseCase,
        private val createDebtorOnly: CreateDebtorOnlyUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(PeopleViewModel::class.java))
            return PeopleViewModel(observeDebtorAccounts, createDebtorOnly) as T
        }
    }
}
