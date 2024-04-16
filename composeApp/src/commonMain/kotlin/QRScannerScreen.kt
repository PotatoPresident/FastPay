import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

expect class QRScannerScreen(modifier: Modifier, onQrCodeScanned: (String) -> Unit) : Screen