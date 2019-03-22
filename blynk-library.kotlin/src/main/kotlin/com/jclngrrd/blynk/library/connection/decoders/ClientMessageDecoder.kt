package com.jclngrrd.blynk.library.connection.decoders

import cc.blynk.server.core.protocol.enums.Command
import cc.blynk.server.core.protocol.enums.Command.*
import cc.blynk.server.core.protocol.model.messages.BinaryMessage
import cc.blynk.server.core.protocol.model.messages.MessageBase
import cc.blynk.server.core.protocol.model.messages.MessageFactory.produce
import cc.blynk.server.core.protocol.model.messages.ResponseMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.apache.logging.log4j.LogManager
import java.nio.charset.StandardCharsets


class ClientMessageDecoder : ByteToMessageDecoder() {

    private val log = LogManager.getLogger(ClientMessageDecoder::class.java)

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (input.readableBytes() < 5) {
            return
        }

        input.markReaderIndex()

        val command = input.readUnsignedByte()
        val messageId = input.readUnsignedShort()

        val message: MessageBase
        if (command == Command.RESPONSE) {
            val responseCode = input.readUnsignedShort()
            message = ResponseMessage(messageId, responseCode)
        } else {
            val length = input.readUnsignedShort()

            if (input.readableBytes() < length) {
                input.resetReaderIndex()
                return
            }

            val buf = input.readSlice(length)
            when (command) {
                GET_ENHANCED_GRAPH_DATA,
                GET_PROJECT_BY_CLONE_CODE,
                LOAD_PROFILE_GZIPPED,
                GET_PROJECT_BY_TOKEN -> {
                    val bytes = ByteArray(buf.readableBytes())
                    buf.readBytes(bytes)
                    message = BinaryMessage(messageId, command, bytes)
                }
                else -> message = produce(messageId, command, buf.toString(StandardCharsets.UTF_8))
            }

        }

        log.trace("Incoming client {}", message)

        out.add(message)
    }
}
