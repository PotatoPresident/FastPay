import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

expect class QRScannerScreen(modifier: Modifier = Modifier, getTransaction: suspend (String) -> Transaction?, onTransactionScanned: (Transaction?) -> Unit) : Screen

fun isValid(value: String): Boolean {
    return value.startsWith("FASTPAY:")
}