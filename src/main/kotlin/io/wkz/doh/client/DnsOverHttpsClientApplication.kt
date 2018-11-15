package io.wkz.doh.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DnsOverHttpsClientApplication

fun main(args: Array<String>) {
    runApplication<DnsOverHttpsClientApplication>(*args)
}
