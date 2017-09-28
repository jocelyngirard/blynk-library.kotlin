package com.jclngrrd.blynk.library.pin

import com.jclngrrd.blynk.library.connection.BlynkConnection
import com.jclngrrd.blynk.library.extension.write

class PinManager(private val connection: BlynkConnection) {

    private val pinList = mutableListOf<VirtualPin>()

    fun virtualPin(number: Int) = pinList.find { it.pin == number } ?: VirtualPin(number, connection::virtualWrite).also { pinList += it }

    fun onEvent(pinEvent: PinEvent) {
        pinList.find { it.pin == pinEvent.pinNumber }?.let { pinHandler ->
            when (pinEvent.operation) {
                PinEvent.Operation.WRITE -> pinHandler.onWrite(pinEvent.values)
                PinEvent.Operation.READ -> pinHandler.onRead()?.let { pinHandler.write(it) }
            }
        }
    }
}
