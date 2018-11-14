package io.wkz.doh.client

import io.wkz.doh.client.netty.LocalExposeServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.InetSocketAddress

@SpringBootApplication
class DnsOverHttpsClientApplication:CommandLineRunner{
    @Autowired
    private lateinit var localExposeServer: LocalExposeServer
    override fun run(vararg args: String?) {
        val address = InetSocketAddress("127.0.0.1", 8053)
        val future = localExposeServer.run(address)
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                localExposeServer.destroy()
            }
        })
        future!!.channel().closeFuture().syncUninterruptibly()
    }
}

fun main(args: Array<String>) {
    runApplication<DnsOverHttpsClientApplication>(*args)
}
