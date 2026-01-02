package com.example.extra_ordinary.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(LocalContext.current))
) {
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importData(context, it) }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { viewModel.exportToUri(context, it) }
    }

    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    var showDatePicker by remember { mutableStateOf(false) }
    var showDefaultRateDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.toggleMoneyVisibility() }) {
                    Icon(
                        if (state.moneyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (state.moneyVisible) "Hide money" else "Show money"
                    )
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Importa Backup") },
                            onClick = {
                                menuExpanded = false
                                importLauncher.launch(arrayOf("*/*"))
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = "Importa Backup"
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Esporta Backup") },
                            onClick = {
                                menuExpanded = false
                                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                                val fileName =
                                    "extraordinary_backup_${LocalDate.now().format(formatter)}.csv"
                                exportLauncher.launch(fileName)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Upload,
                                    contentDescription = "Esporta Backup"
                                )
                            }
                        )
                    }
                }
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