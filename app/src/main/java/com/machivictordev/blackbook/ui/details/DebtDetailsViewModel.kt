package com.machivictordev.blackbook.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.machivictordev.blackbook.domain.model.DebtorDetails
import com.machivictordev.blackbook.domain.repository.DebtMutationResult
import com.machivictordev.blackbook.domain.usecase.AddDebtUseCase
import com.machivictordev.blackbook.domain.usecase.DeleteDebtUseCase
import com.machivictordev.blackbook.domain.usecase.DeleteDebtorUseCase
import com.machivictordev.blackbook.domain.usecase.DeleteRepaymentUseCase
import com.machivictordev.blackbook.domain.usecase.ObserveDebtorDetailsUseCase
import com.machivictordev.blackbook.domain.usecase.RecordRepaymentUseCase
import com.machivictordev.blackbook.domain.usecase.SettleDebtorUseCase
import com.machivictordev.blackbook.domain.usecase.UpdateDebtorNameUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DebtDetailsUiState(
    val isLoading: Boolean = true,
    val isMutating: Boolean = false,
    val details: DebtorDetails? = null,
)

class DebtDetailsViewModel(
    private val debtorId: Long,
    private val observeDetails: ObserveDebtorDetailsUseCase,
    private val addDebtUseCase: AddDebtUseCase,
    private val recordRepaymentUseCase: RecordRepaymentUseCase,
    private val settleDebtorUseCase: SettleDebtorUseCase,
    private val updateDebtorNameUseCase: UpdateDebtorNameUseCase,
    private val deleteDebtorUseCase: DeleteDebtorUseCase,
    private val deleteDebtUseCase: DeleteDebtUseCase,
    private val deleteRepaymentUseCase: DeleteRepaymentUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DebtDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    init {
        viewModelScope.launch {
            observeDetails(debtorId)
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                    _messages.emit("Could not load this account.")
                }
                .collect { details ->
                    _uiState.update { it.copy(isLoading = false, details = details) }
                }
        }
    }

    fun addDebt(amountMinor: Long, note: String) = mutate("Debt added.") { addDebtUseCase(debtorId, amountMinor, note) }

    fun recordRepayment(amountMinor: Long, note: String) = mutate("Payment recorded.") {
        recordRepaymentUseCase(debtorId, amountMinor, note)
    }

    fun settle() = mutate("Account settled.") { settleDebtorUseCase(debtorId) }

    fun updateName(newName: String, onSuccess: () -> Unit) = mutate("Name updated.") {
        val result = updateDebtorNameUseCase(debtorId, newName)
        if (result is DebtMutationResult.Success) {
            onSuccess()
        }
        result
    }

    fun deleteDebtor(onSuccess: () -> Unit) {
        if (_uiState.value.isMutating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            try {
                when (deleteDebtorUseCase(debtorId)) {
                    is DebtMutationResult.Success -> {
                        _messages.emit("Account deleted.")
                        onSuccess()
                    }
                    else -> _messages.emit("Could not delete account.")
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _messages.emit("Could not delete account. Please try again.")
            } finally {
                _uiState.update { it.copy(isMutating = false) }
            }
        }
    }

    fun deleteDebt(debtId: Long) = mutate("Debt record deleted.") {
        deleteDebtUseCase(debtId)
    }

    fun deleteRepayment(repaymentId: Long) = mutate("Payment record deleted.") {
        deleteRepaymentUseCase(repaymentId)
    }

    private fun mutate(successMessage: String, operation: suspend () -> DebtMutationResult) {
        if (_uiState.value.isMutating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            try {
                when (operation()) {
                    is DebtMutationResult.Success -> _messages.emit(successMessage)
                    DebtMutationResult.InvalidAmount -> _messages.emit("Enter an amount greater than zero.")
                    DebtMutationResult.DebtorNotFound -> _messages.emit("This account no longer exists.")
                    DebtMutationResult.NoOutstandingBalance -> _messages.emit("There is no outstanding balance to repay.")
                    is DebtMutationResult.AmountExceedsBalance -> _messages.emit(
                        "Payment exceeds the current outstanding balance.",
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _messages.emit("The change could not be saved. Please try again.")
            } finally {
                _uiState.update { it.copy(isMutating = false) }
            }
        }
    }

    class Factory(
        private val debtorId: Long,
        private val observeDetails: ObserveDebtorDetailsUseCase,
        private val addDebtUseCase: AddDebtUseCase,
        private val recordRepaymentUseCase: RecordRepaymentUseCase,
        private val settleDebtorUseCase: SettleDebtorUseCase,
        private val updateDebtorNameUseCase: UpdateDebtorNameUseCase,
        private val deleteDebtorUseCase: DeleteDebtorUseCase,
        private val deleteDebtUseCase: DeleteDebtUseCase,
        private val deleteRepaymentUseCase: DeleteRepaymentUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(DebtDetailsViewModel::class.java))
            return DebtDetailsViewModel(
                debtorId = debtorId,
                observeDetails = observeDetails,
                addDebtUseCase = addDebtUseCase,
                recordRepaymentUseCase = recordRepaymentUseCase,
                settleDebtorUseCase = settleDebtorUseCase,
                updateDebtorNameUseCase = updateDebtorNameUseCase,
                deleteDebtorUseCase = deleteDebtorUseCase,
                deleteDebtUseCase = deleteDebtUseCase,
                deleteRepaymentUseCase = deleteRepaymentUseCase,
            ) as T
        }
    }
}
