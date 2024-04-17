import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import fastpay.composeapp.generated.resources.Res
import fastpay.composeapp.generated.resources.pay_tab
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

object PayTab : DynamicTab {
    @Composable
    override fun Content() {
        Navigator(PayScreen())
    }

    @OptIn(ExperimentalResourceApi::class)
    override val dynamicOptions: DynamicTabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.pay_tab)
            val outline = rememberVectorPainter(Icons.Outlined.QrCodeScanner)
            val filled = rememberVectorPainter(Icons.Filled.QrCodeScanner)

            return remember {
                DynamicTabOptions(
                    index = 1u,
                    title = title,
                    selectedIcon = filled,
                    unselectedIcon = outline
                )
            }
        }
}

class PayScreen : Screen {
    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow

        FilledTonalButton(onClick = {
//            nav.push(QRScannerScreen() {
//
//            })
        }) {
            Text("Scan")
        }
    }
}