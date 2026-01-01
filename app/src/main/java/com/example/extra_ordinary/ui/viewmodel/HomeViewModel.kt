package com.example.extra_ordinary.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.extra_ordinary.DataStore
import com.example.extra_ordinary.data.model.WorkEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.Locale

data class HomeUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val currentMonthData: Map<Int, WorkEntry> = emptyMap(),
    val defaultRate: Pair<Int, Int>? = null,
    val moneyVisible: Boolean = true,
    val totalHoursFormatted: String = "0 ore",
    val totalIncomeFormatted: String = "€0"
)

class HomeViewModel(private val dataStore: DataStore) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadDataForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val monthForDataStore = yearMonth.monthValue - 1 // Calendar compatibility
            val monthData = dataStore.getMonthData(monthForDataStore, yearMonth.year)
            val defaultRate = dataStore.getDefaultRate(monthForDataStore, yearMonth.year)
            _uiState.update {
                it.copy(
                    currentMonth = yearMonth,
                    currentMonthData = monthData,
                    defaultRate = defaultRate
                )
            }
            updateTotals()
        }
    }

    fun toggleMoneyVisibility() {
        _uiState.update { it.copy(moneyVisible = !it.moneyVisible) }
    }

    fun updateDailyEntry(day: Int, hours: Int, minutes: Int, euros: Int, cents: Int) {
        viewModelScope.launch {
            val currentMonth = _uiState.value.currentMonth
            val monthForDataStore = currentMonth.monthValue - 1 // Calendar compatibility
            dataStore.saveNumber(day, monthForDataStore, currentMonth.year, hours, minutes, euros, cents)
            loadDataForMonth(currentMonth) // Reload data to update UI
        }
    }

    fun updateMonthlyRate(euros: Int, cents: Int) {
        viewModelScope.launch {
            val currentMonth = _uiState.value.currentMonth
            val monthForDataStore = currentMonth.monthValue - 1 // Calendar compatibility
            dataStore.saveDefaultRate(monthForDataStore, currentMonth.year, euros, cents)
            loadDataForMonth(currentMonth) // Reload data to update UI
        }
    }

    private fun updateTotals() {
        val currentData = _uiState.value.currentMonthData
        val defaultRate = _uiState.value.defaultRate

        val totalMinutes = currentData.values.sumOf { it.hours * 60 + it.minutes }
        val totalHours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        val totalHoursFormatted = "$totalHours ore" + if (remainingMinutes > 0) " e $remainingMinutes" else ""

        val totalCents = currentData.values.sumOf { workEntry ->
            var (euros, cents) = workEntry.rateEuros to workEntry.rateCents

            if (euros == 0 && cents == 0) {
                defaultRate?.let {
                    euros = it.first
                    cents = it.second
                }
            }

            val totalTimeInMinutes = workEntry.hours * 60 + workEntry.minutes
            val hourlyRateInCents = euros * 100 + cents
            (totalTimeInMinutes * hourlyRateInCents) / 60
        }

        val totalEuros = totalCents / 100
        val remainingCents = totalCents % 100
        val totalIncomeFormatted =
            if (remainingCents == 0) "€$totalEuros" else "€$totalEuros,${String.format(Locale.getDefault(), "%02d", remainingCents)}"

        _uiState.update {
            it.copy(
                totalHoursFormatted = totalHoursFormatted,
                totalIncomeFormatted = totalIncomeFormatted
            )
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(DataStore(context.applicationContext)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}