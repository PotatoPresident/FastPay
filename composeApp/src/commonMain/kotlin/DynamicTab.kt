import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

interface DynamicTab : Tab {
    val dynamicOptions: DynamicTabOptions
    @Composable get

    override val options: TabOptions
        @Composable
        get() {
            val index = dynamicOptions.index
            val title = dynamicOptions.title

            return remember {
                TabOptions(
                    index = index,
                    title = title,
                    icon = null
                )
            }
        }
}

data class DynamicTabOptions(
    val index: UShort,
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter
)