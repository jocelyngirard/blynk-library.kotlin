package com.jclngrrd.blynk.library.pin

import cc.blynk.server.core.model.enums.PinType

data class PinEvent(
        val operation: Operation,
        val pinType: PinType,
        val pinNumber: Int,
        val values: List<String>
) {

    enum class Operation(val char: Char) {
        READ('r'),
        WRITE('w')
    }
}