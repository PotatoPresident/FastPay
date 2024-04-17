import java.text.NumberFormat
import java.util.Currency

actual fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2
    format.currency = Currency.getInstance("USD")
    return format.format(value)
}