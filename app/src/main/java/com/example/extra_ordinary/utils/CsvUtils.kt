package com.example.extra_ordinary.utils

import com.example.extra_ordinary.data.model.WorkEntry
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

data class BackupData(
    val entries: List<Pair<LocalDate, WorkEntry>>,
    val defaultRates: List<Pair<YearMonth, Pair<Int, Int>>>
)

object CsvUtils {

    fun writeCsvToStream(
        outputStream: OutputStream,
        entries: Map<LocalDate, WorkEntry>,
        defaultRates: Map<YearMonth, Pair<Int, Int>>
    ) {
        outputStream.bufferedWriter().use { out ->
            out.write("Date,Hours,Minutes,Rate(€),Total(€)")
            out.newLine()

            val sortedEntries = entries.toSortedMap()

            sortedEntries.forEach { (date, entry) ->
                val hours = entry.hours
                val minutes = entry.minutes

                var entryRateEuros = entry.rateEuros
                var entryRateCents = entry.rateCents

                if (entryRateEuros == 0 && entryRateCents == 0) {
                    defaultRates[YearMonth.from(date)]?.let {
                        entryRateEuros = it.first
                        entryRateCents = it.second
                    }
                }

                val rate = entryRateEuros + entryRateCents / 100.0
                val total = (hours + minutes / 60.0) * rate

                val line = String.format(
                    Locale.US,
                    "%s,%d,%d,%.2f,%.2f",
                    date,
                    hours,
                    minutes,
                    rate,
                    total
                )
                out.write(line)
                out.newLine()
            }

            val sortedRates = defaultRates.toSortedMap()
            sortedRates.forEach { (yearMonth, rate) ->
                val line = String.format(
                    Locale.US,
                    "#DEFAULT_RATE,%s,%d,%d",
                    yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    rate.first,
                    rate.second
                )
                out.write(line)
                out.newLine()
            }
        }
    }

    fun parseCsv(inputStream: InputStream): BackupData {
        val entries = mutableListOf<Pair<LocalDate, WorkEntry>>()
        val defaultRates = mutableListOf<Pair<YearMonth, Pair<Int, Int>>>()
        inputStream.bufferedReader().useLines { lines ->
            lines.filter { it.isNotBlank() }
                .forEachIndexed { index, line ->
                    val cleanLine = line.trim()
                    if (index == 0 && cleanLine.contains("Date", ignoreCase = true)) return@forEachIndexed

                    if (cleanLine.startsWith("#DEFAULT_RATE")) {
                        try {
                            val tokens = cleanLine.split(",").map { it.trim() }
                            if (tokens.size >= 4) {
                                val yearMonth = YearMonth.parse(tokens[1], DateTimeFormatter.ofPattern("yyyy-MM"))
                                val euros = tokens[2].toInt()
                                val cents = tokens[3].toInt()
                                defaultRates.add(yearMonth to (euros to cents))
                            }
                        } catch (e: Exception) {
                            // ignore malformed lines
                        }
                    } else {
                        try {
                            val tokens = cleanLine.split(",").map { it.trim() }
                            if (tokens.size >= 4) {
                                val date = LocalDate.parse(tokens[0])
                                val hours = tokens[1].toInt()
                                val minutes = tokens[2].toInt()
                                val rateDouble = tokens[3].toDouble()

                                val totalCents = (rateDouble * 100).roundToInt()
                                val rateEuros = totalCents / 100
                                val rateCents = totalCents % 100

                                entries.add(date to WorkEntry(hours, minutes, rateEuros, rateCents))
                            }
                        } catch (e: Exception) {
                            // ignore malformed lines
                        }
                    }
                }
        }
        return BackupData(entries, defaultRates)
    }
}