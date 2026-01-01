package com.example.extra_ordinary.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.extra_ordinary.data.model.WorkEntry
import com.example.extra_ordinary.ui.theme.SoftBlue
import com.example.extra_ordinary.utils.isItalianHoliday
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    moneyVisible: Boolean,
    showDefaultRateDialog: Boolean,
    defaultRate: Pair<Int, Int>?,
    dayNumbers: Map<Int, WorkEntry>,
    modifier: Modifier = Modifier,
    onDismissDefaultRateDialog: () -> Unit,
    onDefaultRateConfirm: (Int, Int) -> Unit,
    onDataRefresh: () -> Unit,
    onUpdateDailyEntry: (Int, Int, Int, Int, Int) -> Unit
) {
    val today = LocalDate.now()
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek
    val daysOfWeek = java.time.DayOfWeek.values().map { it.getDisplayName(TextStyle.SHORT, Locale.ITALIAN) }

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        val initialData = selectedDay?.let { dayNumbers[it] }
        val hasCustomRate = initialData != null && (initialData.rateEuros != 0 || initialData.rateCents != 0)

        TimeAndCurrencyInputDialog(
            initialHours = initialData?.hours,
            initialMinutes = initialData?.minutes,
            initialEuros = if (hasCustomRate) initialData?.rateEuros else defaultRate?.first,
            initialCents = if (hasCustomRate) initialData?.rateCents else defaultRate?.second,
            onDismiss = { showDialog = false },
            onConfirm = { hours, minutes, euros, cents ->
                selectedDay?.let { day ->
                    onUpdateDailyEntry(day, hours, minutes, euros, cents)
                }
                showDialog = false
            }
        )
    }

    if (showDefaultRateDialog) {
        DefaultRateDialog(
            initialEuros = defaultRate?.first,
            initialCents = defaultRate?.second,
            onDismiss = onDismissDefaultRateDialog,
            onConfirm = { euros, cents ->
                onDefaultRateConfirm(euros, cents)
                onDismissDefaultRateDialog()
            }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    fontWeight = FontWeight.Bold,
                    color = if (day.equals("dom", true)) MaterialTheme.colorScheme.error else Color.Unspecified
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize()
        ) {
            val emptyCells = (firstDayOfMonth.value - 1 + 7) % 7
            items(emptyCells) {
                Box(modifier = Modifier.aspectRatio(0.55f))
            }

            items(daysInMonth) { day ->
                val dayOfMonth = day + 1
                val date = yearMonth.atDay(dayOfMonth)
                val isToday = date == today
                val isHoliday = isItalianHoliday(date)

                Box(
                    modifier = Modifier
                        .aspectRatio(0.55f)
                        .border(
                            0.5.dp,
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        .then(if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface) else Modifier)
                        .clickable {
                            selectedDay = dayOfMonth
                            showDialog = true
                        },
                ) {
                    Text(
                        text = dayOfMonth.toString(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp),
                        color = if (isHoliday) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp, bottom = 2.dp, start = 2.dp, end = 2.dp)
                    ) {
                        val workEntry = dayNumbers[dayOfMonth]
                        val hasTime = workEntry?.hasTime() == true
                        var rateEuros = workEntry?.rateEuros ?: 0
                        var rateCents = workEntry?.rateCents ?: 0

                        if (rateEuros == 0 && rateCents == 0) {
                            defaultRate?.let {
                                rateEuros = it.first
                                rateCents = it.second
                            }
                        }
                        val hasCurrency = hasTime && (rateEuros != 0 || rateCents != 0)

                        val timeString = if (hasTime) {
                            workEntry?.let { if (it.minutes == 0) "${it.hours}" else "${it.hours}:${it.minutes}" } ?: " "
                        } else {
                            " "
                        }
                        Text(
                            text = timeString,
                            fontSize = if (moneyVisible) 12.sp else 18.sp,
                            color = if (hasTime) MaterialTheme.colorScheme.secondary else Color.Transparent
                        )
                        if (moneyVisible) {
                            val currencyString = if (hasCurrency) {
                                if (rateCents == 0) "€$rateEuros" else "€$rateEuros,${String.format(Locale.getDefault(), "%02d", rateCents)}"
                            } else {
                                " "
                            }
                            Text(
                                text = currencyString,
                                fontSize = 11.sp,
                                color = if (hasCurrency) SoftBlue else Color.Transparent
                            )

                            val dailyTotalString = if (hasCurrency) {
                                workEntry?.let {
                                    val totalTimeInMinutes = it.hours * 60 + it.minutes
                                    val hourlyRateInCents = rateEuros * 100 + rateCents
                                    val totalCentsForDay = (totalTimeInMinutes * hourlyRateInCents) / 60
                                    val totalEurosForDay = totalCentsForDay / 100
                                    val remainingCentsForDay = totalCentsForDay % 100
                                    if (remainingCentsForDay == 0) "€$totalEurosForDay" else "€$totalEurosForDay,${String.format(Locale.getDefault(), "%02d", remainingCentsForDay)}"
                                }
                            } else {
                                " "
                            }
                            Text(
                                text = dailyTotalString ?: " ",
                                fontSize = 11.sp,
                                color = if (hasCurrency && moneyVisible) MaterialTheme.colorScheme.tertiary else Color.Transparent
                            )
                        }
                    }
                }
            }
        }
    }
}
