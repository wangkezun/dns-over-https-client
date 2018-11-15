package io.wkz.doh.client.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.wkz.doh.client.LocalExposeServerProperties
import io.wkz.doh.client.lazyLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


/**
 *
 * @author 王可尊
 * @since 1.0
 */
@Component
class LocalExposeServer(private val localExposeServerProperties: LocalExposeServerProperties,
                        dnsServerChannelInitializer: DnsServerChannelInitializer) {

    private val log by lazyLogger()
    private val workerGroup = NioEventLoopGroup()
    private var channel: Channel? = null
    private val bootstrap = Bootstrap()
            .group(workerGroup)
            .channel(NioDatagramChannel::class.java)
            .handler(dnsServerChannelInitializer)
            .option(ChannelOption.SO_BROADCAST, true)


    /**
     * 启动服务
     */
    @PostConstruct
    fun run() = GlobalScope.launch {
        var f: ChannelFuture? = null
        try {
            f = bootstrap.bind(localExposeServerProperties.host, localExposeServerProperties.port).syncUninterruptibly()
            channel = f!!.channel()
        } catch (e: Exception) {
            log.error("Netty start error:", e)
        } finally {
            if (f != null && f.isSuccess) {
                log.info("Netty server listening " + localExposeServerProperties.host + " on port " + localExposeServerProperties.port + " and ready for connections...")
            } else {
                log.error("Netty server start up Error!")
            }
        }
    }

    @PreDestroy
    fun destroy() {
        log.info("Shutdown Netty Server...")
        channel?.close()
        workerGroup.shutdownGracefully()
        log.info("Shutdown Netty Server Success!")
    }
}
