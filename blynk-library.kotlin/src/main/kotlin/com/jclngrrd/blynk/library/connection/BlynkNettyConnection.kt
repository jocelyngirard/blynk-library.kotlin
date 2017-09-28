package com.jclngrrd.blynk.library.connection

import cc.blynk.server.core.protocol.enums.Command
import cc.blynk.server.core.protocol.handlers.encoders.MessageEncoder
import cc.blynk.server.core.protocol.model.messages.MessageFactory
import cc.blynk.server.core.protocol.model.messages.common.PingMessage
import cc.blynk.server.core.stats.GlobalStats
import com.jclngrrd.blynk.library.connection.decoders.ClientMessageDecoder
import com.jclngrrd.blynk.library.connection.handler.HardwareHandler
import com.jclngrrd.blynk.library.extension.d
import com.jclngrrd.blynk.library.pin.PinManager
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.apache.logging.log4j.LogManager
import java.util.concurrent.TimeUnit

internal class BlynkNettyConnection(
        override val authToken: String,
        override val host: String,
        override val port: Int
) : BlynkConnection {

    private val log = LogManager.getLogger(BlynkNettyConnection::class.java)

    override val pinManager = PinManager(this)

    private var nioEventLoopGroup: NioEventLoopGroup? = null
    private var channel: Channel? = null

    override fun connect() {
        val bootstrap = Bootstrap().also {
            it.group(NioEventLoopGroup(1).also {
                nioEventLoopGroup = it
            })
            it.channel(NioSocketChannel::class.java)
            it.option(ChannelOption.SO_KEEPALIVE, true)
            it.handler(object : ChannelInitializer<SocketChannel>() {
                public override fun initChannel(channel: SocketChannel) {
                    val pipeline = channel.pipeline()
                    pipeline.addLast(ClientMessageDecoder())
                    pipeline.addLast(MessageEncoder(GlobalStats()))
                    pipeline.addLast(HardwareHandler(pinManager::onEvent))
                }
            })
        }
        this.channel = bootstrap.connect(host, port).sync().channel()
        startPing()
        log.d { "The TCP connection to Blynk server '$host:$port'is now opened(=${channel?.isOpen}) and active(=${channel?.isActive})" }
        send(MessageFactory.produce(1, Command.LOGIN, authToken))
        channel?.closeFuture()?.sync()
    }

    override fun close() {
        nioEventLoopGroup?.shutdownGracefully()
    }

    private fun send(message: Any) {
        log.d { "Send message: $message" }
        channel?.writeAndFlush(message)
    }

    override fun virtualWrite(pin: Int, value: Any?) {
        send(MessageFactory.produce(1, Command.HARDWARE, "vw\u0000$pin\u0000$value"))
    }

    private fun startPing() {
        nioEventLoopGroup?.scheduleAtFixedRate({ send(PingMessage(777)) }, 12, 12, TimeUnit.SECONDS)
    }
}