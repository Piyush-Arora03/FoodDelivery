package com.example.fooddelivery.utils
object StringUtils {
    fun formatCurrency(amount: Double): String {
        val currencyFormater = java.text.NumberFormat.getCurrencyInstance()
        currencyFormater.currency = java.util.Currency.getInstance("USD")
        return currencyFormater.format(amount)
    }
}