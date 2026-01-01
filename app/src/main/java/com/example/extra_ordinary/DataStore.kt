package com.example.extra_ordinary

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.extra_ordinary.data.model.WorkEntry
import java.time.LocalDate
import java.time.YearMonth

class DataStore(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("ExtraordinaryPrefs", Context.MODE_PRIVATE)

    fun saveWorkEntry(day: Int, month: Int, year: Int, workEntry: WorkEntry) {
        sharedPreferences.edit {
            putString("$day-$month-$year", "${workEntry.hours},${workEntry.minutes},${workEntry.rateEuros},${workEntry.rateCents}")
        }
    }

    fun saveWorkEntry(date: LocalDate, workEntry: WorkEntry) {
        saveWorkEntry(date.dayOfMonth, date.monthValue - 1, date.year, workEntry)
    }

    fun getWorkEntry(day: Int, month: Int, year: Int): WorkEntry? {
        val storedString = sharedPreferences.getString("$day-$month-$year", null)
        return storedString?.let {
            val parts = it.split(",")
            if (parts.size == 4) {
                try {
                    WorkEntry(
                        hours = parts[0].toInt(),
                        minutes = parts[1].toInt(),
                        rateEuros = parts[2].toInt(),
                        rateCents = parts[3].toInt()
                    )
                } catch (e: NumberFormatException) {
                    null
                }
            } else {
                null
            }
        }
    }

    fun getMonthData(month: Int, year: Int): Map<Int, WorkEntry> {
        val allEntries = sharedPreferences.all
        val monthData = mutableMapOf<Int, WorkEntry>()
        for ((key, value) in allEntries) {
            val parts = key.split("-")
            if (parts.size == 3 && !key.startsWith("default-rate")) { // Ignore default rate entries
                try {
                    val day = parts[0].toInt()
                    val entryMonth = parts[1].toInt()
                    val entryYear = parts[2].toInt()
                    if (entryMonth == month && entryYear == year) {
                        val storedString = value as String
                        storedString.let {
                            val timeParts = it.split(",")
                            if (timeParts.size == 4) {
                                monthData[day] = WorkEntry(
                                    hours = timeParts[0].toInt(),
                                    minutes = timeParts[1].toInt(),
                                    rateEuros = timeParts[2].toInt(),
                                    rateCents = timeParts[3].toInt()
                                )
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    // Ignore keys that are not in the expected format
                }
            }
        }
        return monthData
    }

    fun saveDefaultRate(month: Int, year: Int, euros: Int, cents: Int) {
        sharedPreferences.edit { putString("default-rate-$month-$year", "$euros,$cents") }
    }

    fun saveDefaultRate(yearMonth: YearMonth, euros: Int, cents: Int) {
        saveDefaultRate(yearMonth.monthValue - 1, yearMonth.year, euros, cents)
    }

    fun getDefaultRate(month: Int, year: Int): Pair<Int, Int>? {
        val storedString = sharedPreferences.getString("default-rate-$month-$year", null)
        return storedString?.let {
            val parts = it.split(",")
            if (parts.size == 2) {
                try {
                    parts[0].toInt() to parts[1].toInt()
                } catch (e: NumberFormatException) {
                    null
                }
            } else {
                null
            }
        }
    }

    fun getAllWorkEntries(): Map<LocalDate, WorkEntry> {
        val allEntries = sharedPreferences.all
        val workEntries = mutableMapOf<LocalDate, WorkEntry>()
        for ((key, value) in allEntries) {
            if (key.startsWith("default-rate")) continue

            val keyParts = key.split("-")
            if (keyParts.size == 3) {
                try {
                    val day = keyParts[0].toInt()
                    val month = keyParts[1].toInt() + 1 // 0-indexed to 1-indexed
                    val year = keyParts[2].toInt()

                    val valueParts = (value as String).split(",")
                    if (valueParts.size == 4) {
                        val entry = WorkEntry(
                            hours = valueParts[0].toInt(),
                            minutes = valueParts[1].toInt(),
                            rateEuros = valueParts[2].toInt(),
                            rateCents = valueParts[3].toInt()
                        )
                        workEntries[LocalDate.of(year, month, day)] = entry
                    }
                } catch (e: Exception) {
                    // Ignore malformed keys/values
                }
            }
        }
        return workEntries
    }

    fun getAllDefaultRates(): Map<YearMonth, Pair<Int, Int>> {
        val allEntries = sharedPreferences.all
        val defaultRates = mutableMapOf<YearMonth, Pair<Int, Int>>()
        for ((key, value) in allEntries) {
            if (!key.startsWith("default-rate-")) continue

            val keyParts = key.removePrefix("default-rate-").split("-")
            if (keyParts.size == 2) {
                try {
                    val month = keyParts[0].toInt() + 1 // 0-indexed to 1-indexed
                    val year = keyParts[1].toInt()

                    val valueParts = (value as String).split(",")
                    if (valueParts.size == 2) {
                        val rate = valueParts[0].toInt() to valueParts[1].toInt()
                        defaultRates[YearMonth.of(year, month)] = rate
                    }
                } catch (e: Exception) {
                    // Ignore malformed keys/values
                }
            }
        }
        return defaultRates
    }


    fun saveNumber(day: Int, month: Int, year: Int, hours: Int, minutes: Int, euros: Int, cents: Int) {
        sharedPreferences.edit { putString("$day-$month-$year", "$hours,$minutes,$euros,$cents") }
    }
}
