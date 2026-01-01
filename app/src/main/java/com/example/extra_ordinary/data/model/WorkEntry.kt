package com.example.extra_ordinary.data.model

data class WorkEntry(
    val hours: Int = 0,
    val minutes: Int = 0,
    val rateEuros: Int = 0,
    val rateCents: Int = 0
) {
    // Helper to check if the entry effectively has no data
    fun isEmpty(): Boolean = hours == 0 && minutes == 0 && rateEuros == 0 && rateCents == 0

    // Helper to check if it has valid time
    fun hasTime(): Boolean = hours > 0 || minutes > 0
}