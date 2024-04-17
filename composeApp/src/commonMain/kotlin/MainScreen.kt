import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.compose.AppTheme
import dev.gitlive.firebase.auth.FirebaseUser
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class MainScreen(val user: FirebaseUser) : Screen {
    @Composable
    override fun Content() {
        AppTheme {
            Scaffold(
                content = {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    NavigationBar {
//                        TabNavigationItem(HomeTab)
                        TabNavigationItem(PayTab)
                        TabNavigationItem(ProfileTab)
                    }
                }
            )
        }
    }

}

@Composable
private fun RowScope.TabNavigationItem(tab: DynamicTab) {
    val tabNavigator = LocalTabNavigator.current
    val icon =
        if (tabNavigator.current == tab) tab.dynamicOptions.selectedIcon else tab.dynamicOptions.unselectedIcon

    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = icon, contentDescription = tab.options.title) })
}
