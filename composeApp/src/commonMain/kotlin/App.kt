import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.compose.AppTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = "926613432043-prjpa6l35mjurjvd0cfc50idu71um1a6.apps.googleusercontent.com"))
    AppTheme {
        Navigator(LoginScreen())
    }
}

