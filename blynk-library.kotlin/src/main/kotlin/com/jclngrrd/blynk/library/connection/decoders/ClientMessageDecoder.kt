package com.jclngrrd.blynk.library.connection.decoders

import cc.blynk.server.core.protocol.enums.Command
import cc.blynk.server.core.protocol.enums.Response
import cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler
import cc.blynk.server.core.protocol.model.messages.MessageBase
import cc.blynk.server.core.protocol.model.messages.MessageFactory.produce
import cc.blynk.server.core.protocol.model.messages.ResponseMessage
import cc.blynk.server.core.protocol.model.messages.ResponseWithBodyMessage
import cc.blynk.server.core.protocol.model.messages.appllication.*
import com.jclngrrd.blynk.library.extension.d
import com.jclngrrd.blynk.library.extension.toByteArray
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.nio.charset.StandardCharsets

class ClientMessageDecoder : ByteToMessageDecoder(), DefaultExceptionHandler {

    private val log = LogManager.getLogger(ClientMessageDecoder::class.java)

    override fun decode(context: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        if (byteBuf.readableBytes() < 5) {
            return
        }

        byteBuf.markReaderIndex()

        val command = byteBuf.readUnsignedByte()
        val messageId = byteBuf.readUnsignedShort()

        val message: MessageBase
        when (command) {
            Command.RESPONSE -> {
                val responseCode = byteBuf.readUnsignedShort()
                message = when (responseCode) {
                    Response.DEVICE_WENT_OFFLINE -> ResponseWithBodyMessage(messageId, Command.RESPONSE, responseCode, byteBuf.readInt())
                    else -> ResponseMessage(messageId, responseCode)
                }
            }
            else -> {
                val length = byteBuf.readUnsignedShort()

                if (byteBuf.readableBytes() < length) {
                    byteBuf.resetReaderIndex()
                    return
                }

                val byteBufSlice = byteBuf.readSlice(length)
                message = when (command) {
                    Command.GET_GRAPH_DATA_RESPONSE -> GetGraphDataBinaryMessage(messageId, byteBufSlice.toByteArray())
                    Command.GET_ENHANCED_GRAPH_DATA -> GetEnhancedGraphDataBinaryMessage(messageId, byteBufSlice.toByteArray())
                    Command.LOAD_PROFILE_GZIPPED -> LoadProfileGzippedBinaryMessage(messageId, byteBufSlice.toByteArray())
                    Command.GET_PROJECT_BY_TOKEN -> GetProjectByTokenBinaryMessage(messageId, byteBufSlice.toByteArray())
                    Command.GET_PROJECT_BY_CLONE_CODE -> GetProjectByCloneCodeBinaryMessage(messageId, byteBufSlice.toByteArray())
                    else -> produce(messageId, command, byteBufSlice.toString(StandardCharsets.UTF_8))
                }

            }
        }
        log.d { "Received message: $message" }
        out.add(message)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        throw IOException("Server closed client connection.")
    }

    override fun exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
        handleGeneralException(context, cause)
        if (cause is IOException) {
            context.close()
            log.d { "End the processus: $cause" }
            System.exit(0)
        }
    }


}
