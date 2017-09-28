package com.jclngrrd.blynk.library.connection.handler

import com.jclngrrd.blynk.library.pin.PinEvent
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

@ChannelHandler.Sharable
class HardwareHandler(private val onEvent: (pinEvent: PinEvent) -> Unit)
    : SimpleChannelInboundHandler<HardwareMessage>() {

    override fun channelRead0(context: ChannelHandlerContext, message: HardwareMessage) {
        val operation = PinEvent.Operation.values().find { it.char == message.body[1] } ?: return
        operation?.let {
            val elements = message.body.split("\u0000")
            onEvent(PinEvent(
                    operation = it,
                    pinType = PinType.getPinType(elements[0][0]),
                    pinNumber = elements[1].toInt(),
                    values = elements.subList(2, elements.size)
            ))
        }
    }

}
