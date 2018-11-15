package io.wkz.doh.client.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.dns.*
import io.wkz.doh.client.cache.DefaultCache
import io.wkz.doh.client.lazyLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


/**
 *
 * @author 王可尊
 * @since 1.0
 */
@Component
class DnsHandler : SimpleChannelInboundHandler<DatagramDnsQuery>() {

    @Autowired
    private lateinit var defaultCache: DefaultCache

    private val log by lazyLogger()
    override fun channelRead0(ctx: ChannelHandlerContext, query: DatagramDnsQuery) {
        // 假数据，域名和ip的对应关系应该放到数据库中
        val ipMap = HashMap<String, ByteArray>()
        ipMap["www.baidu.com."] = byteArrayOf(61, 135.toByte(), 169.toByte(), 125)

        val response = DatagramDnsResponse(query.recipient(), query.sender(), query.id())
        try {
            val dnsQuestion = query.recordAt<DnsRecord>(DnsSection.QUESTION)
            response.addRecord(DnsSection.QUESTION, dnsQuestion)
            log.info("query url:${dnsQuestion.name()}")

            var buf: ByteBuf? = null
            if (ipMap.containsKey(dnsQuestion.name())) {
                buf = Unpooled.wrappedBuffer(ipMap[dnsQuestion.name()])
            } else {
                // TODO  对于没有的域名采用迭代方式
                // buf = Unpooled.wrappedBuffer(new byte[] { 127, 0, 0, 1});
            }
            // TTL设置为10s, 如果短时间内多次请求，客户端会使用本地缓存
            val queryAnswer = DefaultDnsRawRecord(dnsQuestion.name(), DnsRecordType.A, 10, buf!!)
            response.addRecord(DnsSection.ANSWER, queryAnswer)

        } catch (e: Exception) {
            log.error("exception found!", e)
        } finally {
            ctx.writeAndFlush(response)
        }
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.fireChannelActive()
        log.info("active!")
    }
}
