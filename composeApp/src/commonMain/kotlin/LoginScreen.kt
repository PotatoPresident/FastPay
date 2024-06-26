import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class LoginScreen : Screen {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow
        if (auth.currentUser != null) {
            nav.popUntilRoot()
            nav.push(HomeScreen(auth.currentUser!!))
        } else {
            val onFirebaseResult: (Result<FirebaseUser?>) -> Unit = { result ->
                if (result.isSuccess) {
                    val firebaseUser = result.getOrThrow()!!
                    nav.pop()
                    nav.push(HomeScreen(firebaseUser))
                } else {
                    println("Error Result: ${result.exceptionOrNull()?.message}")
                }

            }

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    //Google Sign-In Button and authentication with Firebase
                    GoogleButtonUiContainerFirebase(
                        onResult = onFirebaseResult,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        GoogleSignInButton(
                            modifier = Modifier.width(300.dp).height(44.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = 19.sp
                        ) { this.onClick() }
                    }

                    //Apple Sign-In Button and authentication with Firebase
                    AppleButtonUiContainer(
                        onResult = onFirebaseResult,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        AppleSignInButton(
                            modifier = Modifier.width(300.dp).height(44.dp)
                                .align(Alignment.CenterHorizontally),
                        ) { this.onClick() }
                    }
                }
            }
        }
    }
}