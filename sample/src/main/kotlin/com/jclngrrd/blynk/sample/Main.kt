package com.jclngrrd.blynk.sample

import com.jclngrrd.blynk.library.Blynk
import java.util.*

fun main(args: Array<String>) {

    val authToken = args.firstOrNull() ?: "invalid_token"

    val blynk = Blynk(authToken)

    val v0 = blynk.virtualPin(0)
    v0.onRead = { Date().toString() }
    v0.onWrite = { values ->
        println("V0 new values: $values")
    }

    blynk.connect()
}