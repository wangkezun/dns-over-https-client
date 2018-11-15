package io.wkz.doh.client.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


/**
 *
 * @author 王可尊
 * @since 1.0
 */
@Component
@ConditionalOnMissingBean(DnsCache::class)
class DefaultCache : DnsCache {
	var localCache: Cache<String, List<ByteArray>> = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterWrite(10, TimeUnit.HOURS)
			.removalListener(RemovalListener<String, List<ByteArray>> {
				TODO("not implemented")
			})
			.build()

	override fun set(name: String, record: List<ByteArray>) {
		localCache.put(name, record)
	}

	override fun get(name: String): List<ByteArray>? {
		return localCache.getIfPresent(name)
	}

	override fun evict(name: String): List<ByteArray>? {
		val result = localCache.getIfPresent(name)
		localCache.invalidate(name)
		return result
	}
}
