package io.wkz.doh.client

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import org.xbill.DNS.Lookup
import org.xbill.DNS.SimpleResolver
import java.net.InetAddress
import java.nio.charset.Charset

/**
 *
 * @author 王可尊
 * @since 1.0
 */
class NettyDnsClientTest {
    private val log by lazyLogger()


    @Test
    fun testSpotify() {
        val simpleResolver = SimpleResolver("114.114.114.114")
        val lookup = Lookup("www.baidu.com")
        lookup.setResolver(simpleResolver)
        val run = lookup.run()
        log.info("{}", run[0].rdataToWireCanonical())
        val allByName = InetAddress.getAllByName("www.baidu.com")
        log.info("{}", allByName)
        val client = OkHttpClient.Builder().dns {
            listOf(InetAddress.getByAddress("www.baidu.com", run[0].rdataToWireCanonical()))
        }.build()
        val body = client.newCall(Request.Builder().url("https://www.baidu.com").get().build()).execute().body()
        log.info("{}", body?.bytes()?.toString(Charset.defaultCharset()))

    }
}
