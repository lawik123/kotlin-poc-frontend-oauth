import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JWT(
    val header: Header,
    val payload: Payload,
    val signature: String // base64 encoded
)

@Serializable
data class Header(
    val kid: String,
    val alg: String
)

@Serializable
data class Payload(
    val sub: String,
    val iss: String,
    val organisation: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("family_name")
    val familyName: String,
    val subjectType: String,
    val accountStatus: String,
    val aud: String,
    @SerialName("user_type")
    val userType: String,
    @SerialName("user_id")
    val userId: String,
    val name: String,
    val exp: Long,
    val iat: Long,
    val jti: String
)
