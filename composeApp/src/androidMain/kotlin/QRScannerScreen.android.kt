import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

actual class QRScannerScreen actual constructor(
    modifier: Modifier,
    onQrCodeScanned: (String) -> Unit
) : Screen {
    val modifier = modifier;
    val onQrCodeScanned = onQrCodeScanned;

    @Composable
    override fun Content() {
        val localDensity = LocalDensity.current
        val localConfig = LocalConfiguration.current
        val context = LocalContext.current
        var foundTarget by remember { mutableStateOf(false) }
        var target by remember { mutableStateOf(Rect.Zero) }
        val rect by animateRectAsState(
            targetValue = if (foundTarget) target else with(localDensity) {
                Rect(
                    Offset(
                        localConfig.screenWidthDp.dp.toPx() / 2,
                        localConfig.screenHeightDp.dp.toPx() / 2
                    ), 150.dp.toPx()
                )
            },
            label = "QR Reticule",
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
        var hasPerm by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { hasPerm = it }
        )

        LaunchedEffect(key1 = true) {
            launcher.launch(android.Manifest.permission.CAMERA)
        }

        if (hasPerm) {
            QRCodeComposable {
                it.rawValue?.let { qrCode ->
                    foundTarget = true
                    onQrCodeScanned(qrCode)
                }
                it.boundingBox?.let { box ->
                    target = box.toComposeRect().inflate(8f)
                }
            }

            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val circlePath = Path().apply {
                        addRoundRect(RoundRect(rect.deflate(8f), CornerRadius(30f)))
                    }
                    clipPath(circlePath, clipOp = ClipOp.Difference) {
                        drawRect(SolidColor(Color.Black.copy(alpha = 0.6f)))
                    }
                }

                with(localDensity) {
                    Box(
                        modifier = Modifier
                            .absoluteOffset(x = rect.left.toDp(), y = rect.top.toDp())
                            .width(rect.width.toDp())
                            .height(rect.height.toDp())
                            .animateBorder(
                                strokeWidth = 4.dp,
                                durationMillis = 2000
                            )
                    )
                }
            }
        } else {
//            LocalNavigator.current?.pop();
        }
    }
}

@Composable
fun QRCodeComposable(onQRCodeScanned: (Barcode) -> Unit) {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val lifecycleCameraController = remember {
        LifecycleCameraController(localContext).apply {
            bindToLifecycle(lifecycleOwner)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    val previewView = remember {
        PreviewView(localContext).apply {
            controller = lifecycleCameraController
        }
    }

    LaunchedEffect(previewView) {
        val barcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(localContext),
        ) { result ->
            result.getValue(barcodeScanner)?.filter { !it.rawValue.isNullOrEmpty() }
                ?.forEach { barcode ->
                    onQRCodeScanned(barcode)
                }
        }

        lifecycleCameraController.apply {
            setImageAnalysisAnalyzer(
                Executors.newSingleThreadExecutor(),
                mlKitAnalyzer,
            )
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView },
        )
    }
}