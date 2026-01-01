package com.example.extra_ordinary.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkEntryTest {

    @Test
    fun isEmpty_returnsTrue_onDefault() {
        val entry = WorkEntry()
        assertTrue(entry.isEmpty())
    }

    @Test
    fun isEmpty_returnsFalse_ifHasData() {
        assertFalse(WorkEntry(hours = 1).isEmpty())
        assertFalse(WorkEntry(minutes = 1).isEmpty())
        assertFalse(WorkEntry(rateEuros = 1).isEmpty())
        assertFalse(WorkEntry(rateCents = 1).isEmpty())
    }

    @Test
    fun hasTime_returnsTrue_ifHoursSet() {
        val entry = WorkEntry(hours = 1, minutes = 0)
        assertTrue(entry.hasTime())
    }

    @Test
    fun hasTime_returnsTrue_ifMinutesSet() {
        val entry = WorkEntry(hours = 0, minutes = 30)
        assertTrue(entry.hasTime())
    }

    @Test
    fun hasTime_returnsFalse_ifOnlyRateSet() {
        val entry = WorkEntry(rateEuros = 10)
        assertFalse(entry.hasTime())
    }

    @Test
    fun calculation_logic_integrity() {
        val entry = WorkEntry(hours = 2, minutes = 30, rateEuros = 10, rateCents = 0)
        val total = (entry.hours + entry.minutes / 60.0) * (entry.rateEuros + entry.rateCents / 100.0)
        assertEquals(25.0, total, 0.001)
    }
}