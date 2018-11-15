package io.wkz.doh.client.bootstrap

import io.wkz.doh.client.DnsOverHttpsProperties
import io.wkz.doh.client.lazyLogger
import okhttp3.Dns
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.xbill.DNS.Lookup
import org.xbill.DNS.SimpleResolver
import java.net.InetAddress
import javax.annotation.PostConstruct


/**
 *
 * bootstrap，此类用于将upstream resolver的dns解析,提供给okhttp client使用
 * 这里就是先查询
 * @author 王可尊
 * @since 1.0
 */
@Component
class BootStrapDns(private val dnsOverHttpsProperties: DnsOverHttpsProperties) : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return upstreamList
    }

    private val log by lazyLogger()

    private val upstreamHost: String = getUpstreamHost()

    @Volatile
    private lateinit var upstreamList: List<InetAddress>

    private fun getBootStrapResolver(): String {
        val bootStrapServerList = dnsOverHttpsProperties.bootStrapServerList
        return if (bootStrapServerList.isNotEmpty()) {
            log.info("contain list. random get")
            bootStrapServerList.shuffled().take(1)[0]
        } else {
            log.info("list is empty. use google as default")
            "8.8.8.8:53"
        }
    }

    private fun getResolver(): SimpleResolver {
        val resolver = getBootStrapResolver()
        log.info("bootstrap resolver:{}", resolver)
        val split = resolver.split(":")
        val simpleResolver = SimpleResolver(split[0])
        simpleResolver.setPort(split[1].toInt())
        return simpleResolver
    }

    private fun getLookup(): Lookup {
        return Lookup(upstreamHost).apply { setResolver(getResolver()) }
    }

    private fun getUpstreamHost(): String {
        val regex = Regex("(?<=://)[a-zA-Z.0-9]+(?=/)")
        val matchEntire = regex.find(dnsOverHttpsProperties.upstreamUrl)
        return matchEntire!!.value
    }

    @PostConstruct
    @Scheduled(cron = "0 * * ? * *")
    fun scheduledUpdate() {
        val results = getLookup().run()
        upstreamList = results.map { InetAddress.getByAddress(upstreamHost, it.rdataToWireCanonical()) }.toList()
        log.info("{}", upstreamList)
    }
}
