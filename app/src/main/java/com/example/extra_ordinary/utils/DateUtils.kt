package com.example.extra_ordinary.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

fun isItalianHoliday(date: LocalDate): Boolean {
    val fixedHolidays = listOf(
        1 to Month.JANUARY,
        6 to Month.JANUARY,
        25 to Month.APRIL,
        1 to Month.MAY,
        2 to Month.JUNE,
        15 to Month.AUGUST,
        1 to Month.NOVEMBER,
        8 to Month.DECEMBER,
        25 to Month.DECEMBER,
        26 to Month.DECEMBER
    )

    if (fixedHolidays.contains(date.dayOfMonth to date.month)) {
        return true
    }

    val easter = getEaster(date.year)
    val easterMonday = easter.plusDays(1)

    return date == easter || date == easterMonday || date.dayOfWeek == DayOfWeek.SUNDAY
}

private fun getEaster(year: Int): LocalDate {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val month = (h + l - 7 * m + 114) / 31
    val day = ((h + l - 7 * m + 114) % 31) + 1
    return LocalDate.of(year, month, day)
}
