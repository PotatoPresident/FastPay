import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.firestore.FirebaseFirestore

data class TransactionApproveScreen(
    val transaction: Transaction,
    val account: Account,
    val db: FirebaseFirestore
) : Screen {
    private var isValid: Boolean

    init {
        if (transaction.status != TransactionStatus.PENDING) {
            isValid = false
        }
        if (transaction.to != null && transaction.to != account.id) {
            isValid = false
        }

        isValid = true
    }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow

        if (!isValid) {
            return Column {
                Text("Invalid Transaction")
                Button(onClick = { nav.pop() }) {
                    Text("Back")
                }
            }
        }

        Column {
            Text("Approve Transaction")
            Text("Amount: ${transaction.amount}")
            Text("From: ${transaction.from}")
            Text("To: ${transaction.to}")
            Button(onClick = {

            }) {
                Text("Approve")
            }
            Button(onClick = {

            }) {
                Text("Decline")
            }
        }
    }
}