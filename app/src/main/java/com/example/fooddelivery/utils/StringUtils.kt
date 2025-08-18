package com.example.fooddelivery.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object StringUtils {
    fun formatCurrency(amount: Double): String {
        val currencyFormater = java.text.NumberFormat.getCurrencyInstance()
        currencyFormater.currency = java.util.Currency.getInstance("USD")
        return currencyFormater.format(amount)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String, formatPattern: String = "dd MMM yyyy, hh:mm a"): String {
        return try {
            // 1. Parse the string as a LocalDateTime, since it has no timezone info
            val localDateTime = LocalDateTime.parse(dateString)

            // 2. Assume the parsed time is in UTC and attach that zone information
            val utcDateTime = localDateTime.atZone(ZoneId.of("UTC"))

            // 3. Convert the UTC time to the user's local timezone (e.g., IST)
            val localUserDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())

            // 4. Format it for display
            val formatter = DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault())
            localUserDateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            dateString // Fallback if parsing fails
        }
    }
}