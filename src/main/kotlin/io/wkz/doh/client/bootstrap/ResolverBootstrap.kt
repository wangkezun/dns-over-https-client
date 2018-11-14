package io.wkz.doh.client.bootstrap

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.dns.DatagramDnsResponse
import io.netty.handler.codec.dns.DefaultDnsRawRecord
import io.netty.handler.codec.dns.DnsRecordType
import io.netty.handler.codec.dns.DnsSection
import io.wkz.doh.client.DnsOverHttpsProperties
import io.wkz.doh.client.lazyLogger
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import javax.annotation.PostConstruct


/**
 *
 * bootstrap，此类用于将upstream resolver的dns解析，因为upstream resolver都是url
 * 这里就是先查询
 * @author 王可尊
 * @since 1.0
 */
@Component
class ResolverBootstrap(private val dnsOverHttpsProperties: DnsOverHttpsProperties) {

    private val log by lazyLogger()

    @PostConstruct
    fun postConstruct() {
        val bootStrapServerList = dnsOverHttpsProperties.bootStrapServerList
        val resolver = if (bootStrapServerList.isNotEmpty()) {
            bootStrapServerList.shuffled().take(1)[0]
        } else {
            "8.8.8.8:53"
        }
        log.info("{}", resolver)
        val regex = Regex("(?<=://)[a-zA-Z.0-9]+(?=/)")
        val matchEntire = regex.find(dnsOverHttpsProperties.upstreamUrl)
        log.info("{}", matchEntire!!.value)
//		val group = NioEventLoopGroup()
//		try {
//			val clientBootstrap = Bootstrap()
//
//			clientBootstrap.group(group)
//			clientBootstrap.channel(NioDatagramChannel::class.java)
//			val split = resolver.split(":")
//			clientBootstrap.remoteAddress(InetSocketAddress(split[0], split[1].toInt()))
//			clientBootstrap.handler(object : ChannelInitializer<NioDatagramChannel>() {
//				@Throws(Exception::class)
//				override fun initChannel(socketChannel: NioDatagramChannel) {
//					socketChannel.pipeline().addLast(DatagramDnsQueryEncoder())
//					socketChannel.pipeline().addLast(DatagramDnsResponseDecoder())
//					socketChannel.pipeline().addLast(ClientHandler())
//				}
//			})
//			val channelFuture = clientBootstrap.connect().sync()
//			val defaultDnsQuery = DefaultDnsQuery(1)
//			defaultDnsQuery.addRecord(DnsSection.QUESTION, DefaultDnsQuestion(resolver, DnsRecordType.A))
//
//			channelFuture.channel().writeAndFlush(defaultDnsQuery)
//			channelFuture.channel().closeFuture().sync()
//		} finally {
//			group.shutdownGracefully().sync()
//		}
    }
}

class ClientHandler : SimpleChannelInboundHandler<DatagramDnsResponse>() {
    private val log by lazyLogger()
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramDnsResponse) {
        val count = msg.count(DnsSection.ANSWER)
        var i = 0
        while (count > i) {
            val recordAt = msg.recordAt<DefaultDnsRawRecord>(DnsSection.ANSWER, i)
            val content = recordAt?.content()!!
            when (recordAt.type()) {
                DnsRecordType.A -> {
                    val bytes = ByteArray(content.readableBytes())
                    content.getBytes(content.readerIndex(), bytes)
                    log.info("测试A result: {}.{}.{}.{}",
                             0xff and bytes[0].toInt(),
                             0xff and bytes[1].toInt(),
                             0xff and bytes[2].toInt(),
                             0xff and bytes[3].toInt())
                }
                DnsRecordType.CNAME -> {
                    val bytes = ByteArray(content.readableBytes())
                    content.getBytes(content.readerIndex(), bytes)
                    log.info("CNAME result:{}，{}",bytes,content.readCharSequence(content.readableBytes(),Charset.forName("UTF-8")))
                }
            }
            i++
        }

    }

}
