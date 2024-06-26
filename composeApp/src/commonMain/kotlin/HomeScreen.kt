import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ViewCozy
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.toDuration
import dev.gitlive.firebase.firestore.where
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

class HomeScreen(val user: FirebaseUser) : Screen {
    private val db = Firebase.firestore

    private suspend fun verifyAccount(user: FirebaseUser): Account {
        db.collection("accounts")
            .document(user.uid)
            .get()
            .let {
                if (!it.exists) {
                    val account = Account(
                        id = user.uid,
                        email = user.email!!,
                        name = user.displayName!!,
                        photoUrl = user.photoURL!!,
                        balance = 1000.00
                    )

                    db.collection("accounts")
                        .document(account.id)
                        .set(Account.serializer(), account)
                    return account
                } else {
                    return it.data(Account.serializer()).copy(id = user.uid)
                }
            }
    }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow
        var account by remember { mutableStateOf(Account()) }
        var loaded by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            account = verifyAccount(user)
            loaded = true
        }

        if (!loaded) {
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            TopBar(user)
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    modifier = Modifier,
                    text = formatCurrency(account.balance),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                val string = buildAnnotatedString {
                    appendInlineContent("icon")
                    append(" Available Balance ")
                }
                val inlineContent = remember {
                    mapOf(
                        "icon" to InlineTextContent(
                            Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)
                        ) {
                            Image(
                                painter = rememberVectorPainter(Icons.Outlined.Wallet),
                                contentDescription = "Wallet Icon",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }

                Text(
                    modifier = Modifier.offset(x = 8.dp),
                    text = string,
                    inlineContent = inlineContent,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.size(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ExtendedFloatingActionButton(
                    text = {
                        Text("Send")
                    },
                    icon = {
                        Icon(Icons.Filled.ArrowOutward, contentDescription = "Send")
                    },
                    onClick = {

                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                ExtendedFloatingActionButton(
                    text = {
                        Text("Request")
                    },
                    icon = {
                        Icon(Icons.Filled.SouthWest, contentDescription = "Request")
                    },
                    onClick = {
                        nav.push(RequestScreen(account, db))
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                FloatingActionButton(
                    onClick = {
                        nav.push(QRScannerScreen(getTransaction = { getTransaction(it) }) {
                            if (it != null) {
                                nav.push(TransactionApproveScreen(it, account, db))
                            }
                        })
                    }
                ) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan QR")
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            TransactionHistory()
        }
    }

    @Composable
    fun TopBar(user: FirebaseUser) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            KamelImage(
                resource = asyncPainterResource(data = user.photoURL!!),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = rememberVectorPainter(Icons.Outlined.ViewCozy),
                contentDescription = "Stuff"
            )
            Spacer(modifier = Modifier.size(16.dp))
            Icon(
                painter = rememberVectorPainter(Icons.Outlined.Notifications),
                contentDescription = "Notifications"
            )
        }
    }

    private suspend fun getTransaction(id: String): Transaction? = db.collection("transactions")
        .document(id)
        .get()
        .let {
            if (it.exists) {
                it.data(Transaction.serializer()).copy(id = it.id)
            } else {
                null
            }
        }

    @Composable
    fun TransactionHistory() {
        val scope = rememberCoroutineScope()
        val transcations = remember { mutableStateListOf<Transaction>() }

        LaunchedEffect(Unit) {
            db.collection("transactions")
                .where {
                    all(
                        any(
                            "to" equalTo user.uid,
                            "from" equalTo user.uid
                        )!!,
                        "status" equalTo "COMPLETED"
                    )
                }
                .get()
                .documents
                .map { it.data(Transaction.serializer()).copy(id = it.id) }
                .sortedByDescending { it.date.seconds }
                .forEach { transcations.add(it) }
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text("Transactions", style = MaterialTheme.typography.titleLarge)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                transcations.forEach {
                    item(it.id) {
                        TransactionItem(transaction = it)
                    }
                }
            }
        }
    }

    private @Composable
    fun TransactionItem(transaction: Transaction) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KamelImage(
                resource = asyncPainterResource(data = transaction.photoUrl!!),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .border(2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(transaction.name)
                val time = Instant.fromEpochSeconds(transaction.date.seconds)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                Text(
                    text = "${time.monthNumber}/${time.dayOfMonth}/${time.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Light
                )
            }

//            Spacer(modifier = Modifier.size(8.dp))
//
//            Text(
//                text = transaction.note,
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )

            Spacer(modifier = Modifier.weight(1f))

            if (transaction.to == user.uid) {
                Text(
                    text = "+${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "-${formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

expect fun formatCurrency(value: Double): String
