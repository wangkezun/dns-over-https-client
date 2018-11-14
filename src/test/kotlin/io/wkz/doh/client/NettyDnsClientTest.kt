package io.wkz.doh.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.dns.*
import io.wkz.doh.client.bootstrap.ClientHandler
import org.junit.Test

/**
 *
 * @author 王可尊
 * @since 1.0
 */
class NettyDnsClientTest {
	@Test
	fun test() {
		val group = NioEventLoopGroup()
		try {
			val clientBootstrap = Bootstrap()

			clientBootstrap.group(group)
			clientBootstrap.channel(NioDatagramChannel::class.java)
			clientBootstrap.handler(object : ChannelInitializer<NioDatagramChannel>() {
				@Throws(Exception::class)
				override fun initChannel(socketChannel: NioDatagramChannel) {
					socketChannel.pipeline().addLast(DatagramDnsQueryEncoder())
					socketChannel.pipeline().addLast(DatagramDnsResponseDecoder())
					socketChannel.pipeline().addLast(ClientHandler())
				}
			})
			val channelFuture = clientBootstrap.connect("localhost",8053).sync()
			val defaultDnsQuery = DefaultDnsQuery(1)
			defaultDnsQuery.addRecord(DnsSection.QUESTION, DefaultDnsQuestion("baidu.com", DnsRecordType.A))

			channelFuture.channel().write("123")
			channelFuture.channel().flush()
			channelFuture.channel().closeFuture().sync()
		} finally {
			group.shutdownGracefully().sync()
		}
	}
}
