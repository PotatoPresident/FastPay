import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.compose.md_theme_light_primary
import com.example.compose.md_theme_light_secondary
import fastpay.composeapp.generated.resources.Logo
import fastpay.composeapp.generated.resources.Res
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
data class QRDisplayScreen(val transaction: Transaction) : Screen {
    @Composable
    override fun Content() {
        val logoPainter: Painter = painterResource(Res.drawable.Logo)

        val qrcodePainter: Painter = rememberQrCodePainter("FASTPAY:${transaction.id}") {
            logo {
                painter = logoPainter
                padding = QrLogoPadding.Natural(.1f)
                shape = QrLogoShape.circle()
                size = 0.2f
            }

            shapes {
                ball = QrBallShape.circle()
                darkPixel = QrPixelShape.roundCorners()
                frame = QrFrameShape.roundCorners(.25f)
            }
            colors {
                dark = QrBrush.brush {
                    Brush.linearGradient(
                        0f to md_theme_light_primary,
                        1f to md_theme_light_secondary,
                        end = Offset(it, it)
                    )
                }
                frame = QrBrush.solid(Color.Black)
            }
        }


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(100.dp))
            Box(
                modifier = Modifier.size(300.dp)
                    .background(color = Color.White)
                    .shadow(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(280.dp),
                    painter = qrcodePainter,
                    contentDescription = "QR code referring to the example.com website"
                )
            }
            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = formatCurrency(transaction.amount),
                style = MaterialTheme.typography.displaySmall
            )
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .width(200.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.note,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }

}