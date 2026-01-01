package com.example.extra_ordinary.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.extra_ordinary.ui.components.CalendarView
import com.example.extra_ordinary.ui.components.MonthYearPickerDialog
import com.example.extra_ordinary.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(LocalContext.current))
) {
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    var showDatePicker by remember { mutableStateOf(false) }
    var showDefaultRateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        val monthOffset = pagerState.currentPage - initialPage
        viewModel.loadDataForMonth(YearMonth.now().plusMonths(monthOffset.toLong()))
    }

    if (showDatePicker) {
        MonthYearPickerDialog(
            initialMonth = state.currentMonth.monthValue,
            initialYear = state.currentMonth.year,
            onDismiss = { showDatePicker = false },
            onConfirm = { newMonth, newYear ->
                showDatePicker = false
                val current = state.currentMonth
                val new = YearMonth.of(newYear, newMonth)
                val diff = java.time.temporal.ChronoUnit.MONTHS.between(current, new)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + diff.toInt())
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)).replaceFirstChar { it.uppercase() },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showDatePicker = true }
                )
                if (state.currentMonth != YearMonth.now()) {
                    IconButton(onClick = {
                        val diff = java.time.temporal.ChronoUnit.MONTHS.between(state.currentMonth, YearMonth.now())
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + diff.toInt())
                        }
                    }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Back to Today")
                    }
                }
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state.moneyVisible) {
                    val rateText = state.defaultRate?.let { (euros, cents) ->
                        "${euros},${String.format(Locale.getDefault(), "%02d", cents)}€"
                    } ?: "0,00€"
                    Text(
                        text = rateText,
                        modifier = Modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { showDefaultRateDialog = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Modifica paga oraria mensile")
                }
            }
            IconButton(onClick = { viewModel.toggleMoneyVisibility() }) {
                Icon(
                    if (state.moneyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (state.moneyVisible) "Hide money" else "Show money"
                )
            }
        }

        Surface(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = if (state.moneyVisible) Arrangement.SpaceEvenly else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = "Total Hours")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.totalHoursFormatted,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (state.moneyVisible) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wallet, contentDescription = "Total Income")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = state.totalIncomeFormatted,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            modifier = Modifier.weight(1f)
        ) { page ->
            val monthOffset = page - initialPage
            val yearMonth = YearMonth.now().plusMonths(monthOffset.toLong())
            CalendarView(
                yearMonth = yearMonth,
                moneyVisible = state.moneyVisible,
                showDefaultRateDialog = showDefaultRateDialog,
                defaultRate = state.defaultRate,
                dayNumbers = state.currentMonthData,
                onDismissDefaultRateDialog = { showDefaultRateDialog = false },
                onDefaultRateConfirm = { euros, cents ->
                    viewModel.updateMonthlyRate(euros, cents)
                    showDefaultRateDialog = false
                },
                onDataRefresh = { viewModel.loadDataForMonth(yearMonth) },
                onUpdateDailyEntry = { day, hours, minutes, euros, cents ->
                    viewModel.updateDailyEntry(day, hours, minutes, euros, cents)
                }
            )
        }
    }
}
