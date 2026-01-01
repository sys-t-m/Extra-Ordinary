package com.example.extra_ordinary

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.extra_ordinary.data.model.WorkEntry

class DataStore(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("ExtraordinaryPrefs", Context.MODE_PRIVATE)

    fun saveWorkEntry(day: Int, month: Int, year: Int, workEntry: WorkEntry) {
        sharedPreferences.edit {
            putString("$day-$month-$year", "${workEntry.hours},${workEntry.minutes},${workEntry.rateEuros},${workEntry.rateCents}")
        }
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

    fun saveNumber(day: Int, month: Int, year: Int, hours: Int, minutes: Int, euros: Int, cents: Int) {
        sharedPreferences.edit { putString("$day-$month-$year", "$hours,$minutes,$euros,$cents") }
    }
}