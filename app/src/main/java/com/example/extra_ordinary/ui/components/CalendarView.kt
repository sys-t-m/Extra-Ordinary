package com.example.extra_ordinary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.extra_ordinary.data.model.WorkEntry
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
                    text = day.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = if (day.equals("dom", true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val emptyCells = (firstDayOfMonth.value - 1 + 7) % 7
            items(emptyCells) {
                Box(modifier = Modifier.aspectRatio(0.5f))
            }

            items(daysInMonth) { day ->
                val dayOfMonth = day + 1
                val date = yearMonth.atDay(dayOfMonth)
                val isToday = date == today
                val isHoliday = isItalianHoliday(date)
                val tileShape = RoundedCornerShape(4.dp)

                Box(
                    modifier = Modifier
                        .aspectRatio(0.5f)
                        .clip(tileShape)
                        .background(
                            if (isToday) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .border(
                            width = if (isToday) 2.dp else 1.dp,
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = tileShape
                        )
                        .clickable {
                            selectedDay = dayOfMonth
                            showDialog = true
                        },
                ) {
                    Text(
                        text = dayOfMonth.toString(),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isHoliday) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp),
                        horizontalAlignment = Alignment.End
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
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (hasTime) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        if (moneyVisible && hasCurrency) {
                            val hourlyRateString = if (rateCents == 0) {
                                "€$rateEuros"
                            } else {
                                "€$rateEuros,${String.format(Locale.getDefault(), "%02d", rateCents)}"
                            }
                            Text(
                                text = hourlyRateString,
                                fontSize = 10.sp,
                                lineHeight = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            // 1. Calculate Total Minutes
                            val totalTimeInMinutes = (workEntry?.hours ?: 0) * 60 + (workEntry?.minutes ?: 0)

                            // 2. Calculate Hourly Rate in Cents
                            val hourlyRateInCents = rateEuros * 100 + rateCents

                            // 3. Calculate Actual Earnings: (Time * Rate) / 60
                            val totalCentsForDay = (totalTimeInMinutes * hourlyRateInCents) / 60

                            val totalEurosForDay = totalCentsForDay / 100
                            val remainingCentsForDay = totalCentsForDay % 100

                            // 4. Format Output
                            val dailyTotalString = if (remainingCentsForDay == 0) {
                                "€$totalEurosForDay"
                            } else {
                                "€$totalEurosForDay,${String.format(Locale.getDefault(), "%02d", remainingCentsForDay)}"
                            }
                        Text(
                            text = dailyTotalString,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasCurrency) MaterialTheme.colorScheme.tertiary else Color.Transparent
                        )
                        }
                    }
                }
            }
            val totalCells = emptyCells + daysInMonth
            val remainingCells = (7 - (totalCells % 7)) % 7
            if (remainingCells < 7) {
                items(remainingCells) {
                    Box(modifier = Modifier.aspectRatio(0.5f))
                }
            }
        }
    }
}
