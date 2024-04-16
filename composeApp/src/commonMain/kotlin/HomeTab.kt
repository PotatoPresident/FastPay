import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import fastpay.composeapp.generated.resources.Res
import fastpay.composeapp.generated.resources.home_tab
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

object HomeTab : DynamicTab {

    @Composable
    override fun Content() {
        Text("Hi")
    }

    @OptIn(ExperimentalResourceApi::class)
    override val dynamicOptions: DynamicTabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.home_tab)
            val outline = rememberVectorPainter(Icons.Outlined.Home)
            val filled = rememberVectorPainter(Icons.Filled.Home)

            return remember {
                DynamicTabOptions(
                    index = 0u,
                    title = title,
                    selectedIcon = filled,
                    unselectedIcon = outline
                )
            }
        }
}