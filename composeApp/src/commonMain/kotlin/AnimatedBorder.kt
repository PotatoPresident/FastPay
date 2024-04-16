import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp


val gradientColors = listOf(
    Color.Gray,
    Color.Gray,
    Color.Gray,
    Color.White
)

fun Modifier.animateBorder(
    strokeWidth: Dp,
    durationMillis: Int
) = composed {

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = EaseOutExpo),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val brush = Brush.sweepGradient(gradientColors)

    Modifier.drawWithContent {

        val strokeWidthPx = strokeWidth.toPx()
        val width = size.width
        val height = size.height

        drawContent()

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                cornerRadius = CornerRadius(30F),
                size = Size(width - strokeWidthPx, height - strokeWidthPx),
                style = Stroke(strokeWidthPx)
            )

            // Source
            rotate(angle) {

                drawCircle(
                    brush = brush,
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }

            restoreToCount(checkPoint)
        }
    }
}