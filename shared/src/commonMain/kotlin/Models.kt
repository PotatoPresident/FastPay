import kotlinx.serialization.Serializable

@Serializable
data class User(
    var id: Int,
    var name: String,
    var money: Double
)