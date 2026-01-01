package com.example.extra_ordinary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.extra_ordinary.ui.components.TimeAndCurrencyInputDialog
import com.example.extra_ordinary.ui.theme.ExtraordinaryTheme
import java.time.LocalDate

class WidgetConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExtraordinaryTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    val dataStore = DataStore(this@WidgetConfigurationActivity)

                    val today = LocalDate.now()
                    val day = today.dayOfMonth
                    val month = today.monthValue - 1
                    val year = today.year

                    val initialData = dataStore.getWorkEntry(day, month, year)

                    TimeAndCurrencyInputDialog(
                        initialHours = initialData?.hours,
                        initialMinutes = initialData?.minutes,
                        initialEuros = initialData?.rateEuros,
                        initialCents = initialData?.rateCents,
                        onDismiss = { finish() },
                        onConfirm = { hours, minutes, euros, cents ->
                            dataStore.saveNumber(day, month, year, hours, minutes, euros, cents)
                            finish()
                        }
                    )
                }
            }
        }
    }
}
