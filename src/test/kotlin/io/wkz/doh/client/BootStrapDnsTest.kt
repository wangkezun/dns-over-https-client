package io.wkz.doh.client

import io.wkz.doh.client.bootstrap.BootStrapDns
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author 王可尊
 * @since 1.0
 */
@SpringBootTest
class BootStrapDnsTest {
	@Autowired
	private lateinit var bootStrapDns: BootStrapDns

	@Autowired
	private lateinit var dnsOverHttpsProperties: DnsOverHttpsProperties

	@Test
	fun testUpstreamNotNull() {
		val lookup = bootStrapDns.lookup(dnsOverHttpsProperties.upstreamUrl)
		assertNotNull(lookup)
	}
}
