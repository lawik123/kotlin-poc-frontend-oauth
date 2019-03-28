import kotlinx.serialization.json.Json
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.js.Date
import kotlin.math.floor

// Error constants
const val ERROR_NO_TOKEN = "No token found"
const val ERROR_DECODE_TOKEN = "Unable to decode token"
const val ERROR_EXPIRED_TOKEN = "Expired token"
const val ERROR_UNMATCHED_STATE = "Unmatched state"

// LocalStorage constants
const val LS_STATE = "state"
const val LS_JWT = "jwt"

// Redirect URI hash prefix
const val HASH_PREFIX = "id_token="

// HTML elements
val message = document.getElementById("message") as HTMLParagraphElement
val login = document.getElementById("login") as HTMLAnchorElement
val loader = document.getElementById("loader") as HTMLDivElement


fun main() {
    login.onclick = { it.preventDefault(); initiateOAuth() }

    val hash = window.location.hash.substring(1)
    val state = localStorage[LS_STATE]
    if (hash.startsWith(HASH_PREFIX) && state != null) { // check whether user is in OAuth flow
        val hashValues =
            hash.split("&").map { it.split("=")[1] } // get the query parameter values [0] = token, [1] = state
        if (hashValues[1] !== state) { // check whether the states match
            showError(ERROR_UNMATCHED_STATE)
        } else {
            val jwtString = hashValues[0]
            decodeJWT(jwtString) { jwt ->
                localStorage[LS_JWT] = jwtString
                showJWT(jwt)
            }
        }
        localStorage.removeItem(LS_STATE)
    } else {
        // check whether user has a token in LocalStorage
        localStorage[LS_JWT]?.let {
            decodeJWT(it) { jwt ->
                if (floor(Date.now() / 1000.0) >= jwt.payload.exp) { // check whether token has expired
                    showError(ERROR_EXPIRED_TOKEN)
                } else {
                    showJWT(jwt)
                }
            }
        } ?: showError(ERROR_NO_TOKEN)
    }
    loader.hidden = true
    message.hidden = false
}

fun initiateOAuth() {
    val state = generateRandomString()
    localStorage[LS_STATE] = state
    window.location.href = AUTH_SERVER +
            "?response_type=$RESPONSE_TYPE" +
            "&client_id=$CLIENT_ID" +
            "&redirect_uri=$REDIRECT_URI" +
            "&provider=$PROVIDER" +
            "&scope=$SCOPE" +
            "&state=$state"
}

fun showError(error: String) {
    message.firstChild!!.nodeValue = "$error "
    login.hidden = false
}

fun showJWT(jwt: JWT) {
    val pre = document.createElement("pre") as HTMLPreElement
    pre.textContent = Json(indented = true).stringify(JWT.serializer(), jwt)
    message.appendChild(pre)
    login.hidden = true
}

/**
 * Tries to decode/deserialize a [JWT], if successful, the provided body function will be invoked
 * if not successful, an error message will be shown.
 *
 * @param jwtString jwtString the three part base64 encoded jwt string separated by . (full stop)
 * @param body function to invoke if decoding/deserialization is successful, the [JWT] is passed as the parameter.
 */
fun decodeJWT(jwtString: String, body: (JWT) -> Unit) {
    try {
        val jwt = decodeJWT(jwtString)
        body(jwt)
    } catch (e: Throwable) {
        showError(ERROR_DECODE_TOKEN)
    }
}




