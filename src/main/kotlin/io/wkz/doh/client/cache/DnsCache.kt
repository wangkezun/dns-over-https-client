package io.wkz.doh.client.cache

/**
 * dns缓存接口，可以通过实现这个接口来实现自己定义的缓存实现
 * @author 王可尊
 * @since 1.0
 */
interface DnsCache {
    fun set(name: String, record: List<ByteArray>)
    fun get(name: String): List<ByteArray>?
    fun evict(name: String): List<ByteArray>?
}
