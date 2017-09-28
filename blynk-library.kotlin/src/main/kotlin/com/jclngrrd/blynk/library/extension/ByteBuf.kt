package com.jclngrrd.blynk.library.extension

import io.netty.buffer.ByteBuf

fun ByteBuf.toByteArray(): ByteArray {
    val bytes = ByteArray(readableBytes())
    readBytes(bytes)
    return bytes
}