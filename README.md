# blynk-library.kotlin
Blynk library implementation for Kotlin/Java

[![](https://jitpack.io/v/jocelyngirard/blynk-library.kotlin.svg)](https://jitpack.io/#jocelyngirard/blynk-library.kotlin)

Usage: 
```kotlin
val blynk = Blynk(authToken) // Connects to Blynk cloud
// or
val blynk = Blynk(authToken, customHost, customPort) // Connects to custom server

// Get virtual pin 0
val v0 = blynk.virtualPin(0)
v0.onRead = { 
  println("Return data to display on V0")
  "Some data"
}
v0.onWrite = { values ->
  println("V0 has new values: $values") 
}

v0.write("Push data to V0")
```
