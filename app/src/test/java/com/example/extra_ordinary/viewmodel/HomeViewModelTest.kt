package com.example.extra_ordinary.viewmodel

import com.example.extra_ordinary.DataStore
import com.example.extra_ordinary.data.model.WorkEntry
import com.example.extra_ordinary.rules.MainDispatcherRule
import com.example.extra_ordinary.ui.viewmodel.HomeViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.YearMonth

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dataStore = mockk<DataStore>(relaxed = true)
    private val viewModel = HomeViewModel(dataStore)

    @Test
    fun `initial state should use current month`() {
        val currentState = viewModel.uiState.value
        assertEquals(YearMonth.now(), currentState.currentMonth)
    }

    @Test
    fun `loadData calculates correctly mixing custom and default rates`() = runTest {
        val testMonth = YearMonth.of(2024, 1)
        
        // Setup Data: 2 hours @ 10€, 1 hour @ Default (20€)
        val entry1 = WorkEntry(hours = 2, minutes = 0, rateEuros = 10, rateCents = 0)
        val entry2 = WorkEntry(hours = 1, minutes = 0, rateEuros = 0, rateCents = 0)
        
        val fakeMap = mapOf(1 to entry1, 2 to entry2)
        val fakeDefaultRate = 20 to 0
        
        // Mock DataStore responses
        every { dataStore.getMonthData(0, 2024) } returns fakeMap
        every { dataStore.getDefaultRate(0, 2024) } returns fakeDefaultRate
        
        // Execute
        viewModel.loadDataForMonth(testMonth)
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals("3 ore", state.totalHoursFormatted)
        assertEquals("€40", state.totalIncomeFormatted) // (2*10) + (1*20) = 40
        assertEquals(testMonth, state.currentMonth)
    }

    @Test
    fun `toggleMoneyVisibility switches boolean state`() {
        // Initial is true
        assertEquals(true, viewModel.uiState.value.moneyVisible)

        viewModel.toggleMoneyVisibility()
        assertEquals(false, viewModel.uiState.value.moneyVisible)

        viewModel.toggleMoneyVisibility()
        assertEquals(true, viewModel.uiState.value.moneyVisible)
    }
}
