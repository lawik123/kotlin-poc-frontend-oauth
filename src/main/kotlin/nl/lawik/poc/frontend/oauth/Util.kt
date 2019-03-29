package nl.lawik.poc.frontend.oauth

import kotlinx.serialization.json.Json
import kotlin.browser.window
import kotlin.random.Random

/**
 * List of characters used for creating a random state with the [generateRandomString] function.
 */
private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

/**
 * Decodes/deserializes a string to a [JWT], the signature remains base64 encoded.
 *
 * @param jwtString the three part base64 encoded jwt string separated by . (full stop)
 *
 * @return the decoded/deserialized [JWT], the signature remains base64 encoded
 */
fun decodeJWT(jwtString: String): JWT {
    try {
        val separated = jwtString.split(".")
        val header = Json.parse(Header.serializer(), window.atob(separated[0]))
        val payload = Json.parse(Payload.serializer(), window.atob(separated[1]))
        return JWT(header, payload, separated[2])
    } catch (e: Throwable) {
        throw e
    }
}

/**
 * Generates a random 15 length string used for creating a random state using the characters defined in [charPool]
 *
 * @return A random 15 length string consisting of the characters defined in defined in [charPool]
 */
fun generateRandomString(): String {
    return (1..15).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
}
