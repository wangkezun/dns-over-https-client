package io.wkz.doh.client

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author 王可尊
 * @since 1.0
 */
@ConfigurationProperties("dns.server")
class LocalExposeServerProperties {
	lateinit var host: String
	var port: Int = 8053
}
