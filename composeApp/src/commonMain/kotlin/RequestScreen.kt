import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

data class RequestScreen(val account: Account, val db: FirebaseFirestore) : Screen {
    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow
        var amount by remember { mutableStateOf("") }
        var label by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        var isError by rememberSaveable { mutableStateOf(false) }
        fun validate(text: String) {
            isError = text.toDoubleOrNull() == null
        }

        var processing by remember { mutableStateOf(false) }

        if (processing) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.width(100.dp).align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            return
        }


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(100.dp))
            Text("Request Payment", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(30.dp))
            TextField(
                value = amount,
                onValueChange = {
                    amount = it.filter { it.isDigit() || it == '.' }
                    validate(amount)
                },
                label = { Text("Payment Amount") },
                placeholder = { Text("Enter amount") },
                prefix = { Text("$") },
                leadingIcon = { Icon(Icons.Default.Money, contentDescription = "Money Icon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                isError = isError,
            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                placeholder = { Text("Enter label") },
                leadingIcon = { Icon(Icons.AutoMirrored.Default.Notes, contentDescription = "Label Icon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                singleLine = true
            )
            Spacer(modifier = Modifier.size(50.dp))
            ExtendedFloatingActionButton(
                text = { Text("Request") },
                icon = { Icon(Icons.Default.Sell, contentDescription = "Money Icon") },
                onClick = {
                    if (!isError && label.isNotBlank()) {
                        scope.launch {
                            processing = true
                            val transaction = submitTransactionRequest(amount.toDouble(), label)
                            nav.pop()
                            nav.push(QRDisplayScreen(transaction))
                        }
                    }
                },
            )
        }
    }

    private suspend fun submitTransactionRequest(amount: Double, label: String): Transaction {
        val transaction = Transaction(
            to = account.id,
            amount = amount,
            note = label,
            photoUrl = account.photoUrl,
            name = account.name,
        )
        val new = db.collection("transactions").add(transaction)
        return new.get().data(Transaction.serializer()).copy(id = new.id)
    }
}