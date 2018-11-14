package io.wkz.doh.client.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.wkz.doh.client.lazyLogger
import org.springframework.stereotype.Component
import java.net.InetSocketAddress





/**
 *
 * @author 王可尊
 * @since 1.0
 */
@Component
class LocalExposeServer {

	private val log by lazyLogger()
	private val bossGroup = NioEventLoopGroup()
	private val workerGroup = NioEventLoopGroup()
	private var channel: Channel? = null

	/**
	 * 启动服务
	 */
	fun run(address: InetSocketAddress): ChannelFuture? {

		var f: ChannelFuture? = null
		try {
			val b = Bootstrap()
			b.group(workerGroup)
					.channel(NioDatagramChannel::class.java)
                    .handler(DnsServerChannelInitializer())
					.option(ChannelOption.SO_BROADCAST, true)

			f = b.bind(8053).syncUninterruptibly()
			channel = f!!.channel()
		} catch (e: Exception) {
			log.error("Netty start error:", e)
		} finally {
			if (f != null && f.isSuccess) {
				log.info("Netty server listening " + address.hostName + " on port " + address.port + " and ready for connections...")
			} else {
				log.error("Netty server start up Error!")
			}
		}

		return f
	}

	fun destroy() {
		log.info("Shutdown Netty Server...")
		channel?.close()
		workerGroup.shutdownGracefully()
		bossGroup.shutdownGracefully()
		log.info("Shutdown Netty Server Success!")
	}
}
