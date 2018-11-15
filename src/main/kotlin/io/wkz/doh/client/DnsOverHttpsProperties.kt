package io.wkz.doh.client

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author 王可尊
 * @since 1.0
 */
@ConfigurationProperties("dns.bootstrap")
class DnsOverHttpsProperties {
    lateinit var bootStrapServerList: List<String>
    var upstreamUrl: String = "https://dns.google.com/resolve?"
}
