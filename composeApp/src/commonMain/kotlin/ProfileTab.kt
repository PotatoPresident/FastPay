import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import fastpay.composeapp.generated.resources.Res
import fastpay.composeapp.generated.resources.profile_tab
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

object ProfileTab : DynamicTab {
    @Composable
    override fun Content() {
        Text("You have 0 money")
    }

    @OptIn(ExperimentalResourceApi::class)
    override val dynamicOptions: DynamicTabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.profile_tab)
            val outline = rememberVectorPainter(Icons.Outlined.AccountCircle)
            val filled = rememberVectorPainter(Icons.Filled.AccountCircle)

            return remember {
                DynamicTabOptions(
                    index = 2u,
                    title = title,
                    selectedIcon = filled,
                    unselectedIcon = outline
                )
            }
        }
}