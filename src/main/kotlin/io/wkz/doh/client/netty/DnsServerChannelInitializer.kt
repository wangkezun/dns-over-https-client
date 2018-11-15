package io.wkz.doh.client.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


/**
 *
 * @author 王可尊
 * @since 1.0
 */
@Component
class DnsServerChannelInitializer : ChannelInitializer<NioDatagramChannel>() {
    @Autowired
    private lateinit var dnsHandler: DnsHandler

    override fun initChannel(ch: NioDatagramChannel) {
        // 解码编码
        ch.pipeline().addLast(DatagramDnsQueryDecoder())
        ch.pipeline().addLast(DatagramDnsResponseEncoder())

        ch.pipeline().addLast(dnsHandler)
    }
}
