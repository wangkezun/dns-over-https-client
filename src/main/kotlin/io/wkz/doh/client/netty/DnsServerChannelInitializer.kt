package io.wkz.doh.client.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder



/**
 *
 * @author 王可尊
 * @since 1.0
 */
class DnsServerChannelInitializer: ChannelInitializer<NioDatagramChannel>() {
	override fun initChannel(ch: NioDatagramChannel) {
		// 解码编码
		ch.pipeline().addLast(DatagramDnsQueryDecoder())
		ch.pipeline().addLast(DatagramDnsResponseEncoder())

		ch.pipeline().addLast(DnsHandler())
	}
}
