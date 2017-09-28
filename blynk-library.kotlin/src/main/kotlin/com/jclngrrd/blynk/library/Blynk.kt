package com.jclngrrd.blynk.library

import com.jclngrrd.blynk.library.connection.BlynkConnection
import com.jclngrrd.blynk.library.connection.BlynkNettyConnection

class Blynk(
        authToken: String,
        host: String = Blynk.DEFAULT_HOST,
        port: Int = Blynk.DEFAULT_PORT,
        private val connection: BlynkConnection = BlynkNettyConnection(authToken, host, port))
    : BlynkConnection by connection {

    companion object {
        const val DEFAULT_HOST = "blynk-cloud.com"
        const val DEFAULT_PORT = 8442
    }

}
