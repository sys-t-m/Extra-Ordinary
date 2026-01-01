package com.example.extra_ordinary.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class DateUtilsTest {

    @Test
    fun `fixed holidays should return true`() {
        assertTrue(isItalianHoliday(LocalDate.of(2024, Month.DECEMBER, 25)))
        assertTrue(isItalianHoliday(LocalDate.of(2024, Month.JANUARY, 1)))
        assertTrue(isItalianHoliday(LocalDate.of(2024, Month.AUGUST, 15)))
    }

    @Test
    fun `sundays should return true`() {
        assertTrue(isItalianHoliday(LocalDate.of(2024, Month.JANUARY, 14)))
    }

    @Test
    fun `dynamic holidays should return true`() {
        // Easter 2024
        assertTrue(isItalianHoliday(LocalDate.of(2024, Month.MARCH, 31)))
        // Easter Monday 2024
        assertTrue(isItalianHoliday(LocalDate.of(2024, Month.APRIL, 1)))

        // Easter 2025
        assertTrue(isItalianHoliday(LocalDate.of(2025, Month.APRIL, 20)))
        // Easter Monday 2025
        assertTrue(isItalianHoliday(LocalDate.of(2025, Month.APRIL, 21)))
    }

    @Test
    fun `working days should return false`() {
        assertFalse(isItalianHoliday(LocalDate.of(2024, Month.JUNE, 18)))
    }
}