package io.wkz.doh.client

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 *
 * @author 王可尊
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(DnsOverHttpsProperties::class)
class AutoConfiguration {
}
