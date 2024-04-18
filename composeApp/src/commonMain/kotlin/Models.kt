import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @kotlinx.serialization.Transient
    var id: String = "",
    var email: String = "",
    var name: String = "",
    var photoUrl: String = "",
    var balance: Double = 0.0
)

@Serializable
data class Transaction(
    @kotlinx.serialization.Transient
    val id: String = "",
    val from: String? = null,
    val to: String? = null,
    val amount: Double = 0.0,
    val name: String,
    val note: String,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val type: TransactionType = TransactionType.PAYMENT,
    val photoUrl: String?,
    val date: Timestamp = Timestamp.now(),
)

enum class TransactionType {
    PAYMENT,
    SUBSCRIPTION,
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    DECLINED,
    FAILED
}
