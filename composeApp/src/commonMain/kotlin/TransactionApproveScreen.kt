import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

data class TransactionApproveScreen(
    val transaction: Transaction, val account: Account, val db: FirebaseFirestore
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
        val scope = rememberCoroutineScope()

        if (!isValid) {
            return Column {
                Text("Invalid Transaction")
                Button(onClick = { nav.pop() }) {
                    Text("Back")
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(100.dp))
            Text(
                text = "Approve Transaction",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.size(30.dp))

            ElevatedCard(
                modifier = Modifier.size(200.dp, 150.dp).align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = formatCurrency(transaction.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                KamelImage(
                    resource = asyncPainterResource(data = transaction.photoUrl!!),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Send to: ${transaction.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ExtendedFloatingActionButton(
                    text = { Text("Approve") },
                    icon = { Icon(Icons.Default.Done, contentDescription = "Approve") },
                    onClick = {
                        scope.launch {
                            val status = processTransaction(transaction, account, db)
                            updateTransaction(transaction, account, status, db)
                            nav.popUntilRoot()
                        }
                    })
                Spacer(modifier = Modifier.size(16.dp))
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.error,
                    text = { Text("Decline") },
                    icon = { Icon(Icons.Default.Cancel, contentDescription = "Decline") },
                    onClick = {
                        scope.launch {
                            updateTransaction(transaction, account, TransactionStatus.DECLINED, db)
                            nav.popUntilRoot()
                        }
                    })
            }
        }
    }
}

suspend fun updateTransaction(
    transaction: Transaction,
    account: Account,
    status: TransactionStatus,
    db: FirebaseFirestore
) {
    var updatedTransaction = transaction.copy(status = status)
    if (status == TransactionStatus.COMPLETED) {
        updatedTransaction = updatedTransaction.copy(from = account.id)
    }

    db.collection("transactions")
        .document(transaction.id)
        .set(
            Transaction.serializer(),
            updatedTransaction
        )
}


suspend fun processTransaction(
    transaction: Transaction,
    account: Account,
    db: FirebaseFirestore
): TransactionStatus {
    if (transaction.status != TransactionStatus.PENDING) {
        return transaction.status
    }
    if (transaction.to == null) {
        return TransactionStatus.FAILED
    }
    if (transaction.from != null && transaction.from != account.id) {
        return TransactionStatus.PENDING
    }

    db.collection("accounts")
        .document(transaction.to)
        .get()
        .let {
            if (!it.exists) {
                return TransactionStatus.FAILED
            }

            val targetAccount = it.data(Account.serializer())
            if (account.balance < transaction.amount) {
                return TransactionStatus.FAILED
            }

            db.collection("accounts")
                .document(transaction.to)
                .set(
                    Account.serializer(),
                    targetAccount.copy(balance = targetAccount.balance + transaction.amount)
                )

            db.collection("accounts")
                .document(account.id)
                .set(
                    Account.serializer(),
                    account.copy(balance = account.balance - transaction.amount)
                )
        }

    return TransactionStatus.COMPLETED
}