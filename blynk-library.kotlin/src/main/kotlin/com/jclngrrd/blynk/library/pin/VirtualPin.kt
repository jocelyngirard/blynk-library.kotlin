package com.jclngrrd.blynk.library.pin

data class VirtualPin(
        val pin: Int,
        val write: (pin: Int, value: Any?) -> Unit,
        var onRead: () -> String? = { null },
        var onWrite: (value: List<String>) -> Unit = {}
)