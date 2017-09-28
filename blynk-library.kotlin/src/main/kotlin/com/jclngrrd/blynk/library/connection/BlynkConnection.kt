package com.jclngrrd.blynk.library.connection

import com.jclngrrd.blynk.library.pin.PinManager

interface BlynkConnection {

    val authToken: String
    val host: String
    val port: Int

    val pinManager: PinManager

    fun connect()
    fun close()

    fun virtualPin(number: Int) = pinManager.virtualPin(number)
    fun virtualWrite(pin: Int, value: Any?)
}