# Kotlin OAuth PoC
This  project is a PoC in which an OAuth implicit grant flow with the Topicus Education wise-r platform is applied using Kotlin (JS), it showcases the following features:
* The use of browser APIs:
    * LocalStorage
    * window.location
    * window.atob
* Mutating the DOM
* Decoding/deserializing jwt

NOTE: It is assumed that you have basic knowledge of Kotlin, JavaScript and OAuth (implicit grant)

## Installation
1. Clone this repository
2. You need a way to serve the files on http://localhost:8080, you could use the `node-static` CLI for this, which you can install by following these steps:
    1. Make sure you have npm installed.
    2. Install `node-static` globally using the following command: `npm install -g node-static`
    3. You are now able to serve a folder on http:localhost:8080 by running the following command in said folder: `static`
3. (OPTIONAL): You can configure the OAuth parameters in `Config.kt`, though you will be able to run the project using the default settings.

NOTE: Due to the OAuth redirect uri, this example will only work if it runs on http://localhost:8080.

NOTE: You will only be able to login in the authorization server if you have a Topicus Education KeyHub account.

## Running the project
1. Run the following command in the root directory of the project: `gradlew clean build`. 
This will download gradle, download the required dependencies and compile(transpile) Kotlin to JavaScript 
and put the `index.html` and JS files in a `web` folder in the root directory.
2. Serve the contents of the web folder, if you installed `node-static`, you can do this by running the `static` command in the web folder.
3. Open `http://localhost:8080` in your browser. You are now able to use the example application. 
    1. if you have a valid jwt it will show you its contents. 
    2. If you don't have a valid jwt (or if you have an invalid state parameter) you will see an error message and an option to login (which starts an OAuth implicit grant flow).
    
## About

### The use browser APIs
When the user opens the application, it will check whether the user is in an OAuth flow. 
It does this by checking the current window.location hash and by checking whether a state key/value has been set in the LocalStage.
The window.location (and other window.* functions such as window.atob) and LocalStorage APIs are supported out of the box in the Kotlin standard library by the following imports:
```kotlin
import kotlin.browser.localStorage
import kotlin.browser.window
```
See `App.kt` and its comments for the full flow of the application.

### Mutating the DOM
DOM manipulation is supported out of the box in the Kotlin standard library, the API is similar to the JavaScript API for DOM manipulation.

Below is a comparison between Kotlin and JavaScript retrieving the message Paragraph element and setting its first child node value to an error message.

Kotlin:
```kotlin
val message = document.getElementById("message") as HTMLParagraphElement

fun showError(error: String) {
    message.firstChild!!.nodeValue = "$error " // !! assumes you are sure firstChild doesn't return null.
    // ...
}
```

JavaScript:
```javascript
var message = document.getElementById("message");

function showError(error){
    message.firstChild.nodeValue = error + " "
    // ...
}
```

As you can see the APIs are very similar. 

### Decoding/deserializing jwt
The `Util.kt` file has a `decodeJWT` function which will decode the provided string using the `window.atob` function and then deserialize it using the `kotlinx.serialization` library.
The `JWT.kt` classes are annotated with the `@Serializable` annotation which allows the `kotlinx.serialization` library to (de)serialize the classes using the `Json.parse` and `Json.stringify` function along with the static `JWT.serializer` function which is provided by the `@Serializable` annotation.
The signature remains base64 encoded.


