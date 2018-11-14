package io.wkz.doh.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject

/**
 *
 * @author 王可尊
 * @since 1.0
 * @see [](https://stackoverflow.com/questions/34416869/idiomatic-way-of-logging-in-kotlin)
 */
/**
 * Return logger for Java class, if companion object fix the name
 */
fun <T : Any> logger(forClass: Class<T>): Logger {
    return LoggerFactory.getLogger(unwrapCompanionClass(forClass).name)
}

/**
 * unwrap companion class to enclosing class given a Java Class
 */
fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

/**
 * unwrap companion class to enclosing class given a Kotlin Class
 */
fun <T : Any> unwrapCompanionClass(ofClass: KClass<T>): KClass<*> {
    return unwrapCompanionClass(ofClass.java).kotlin
}

/**
 * Return logger for Kotlin class
 */
fun <T : Any> logger(forClass: KClass<T>): Logger {
    return logger(forClass.java)
}

// return logger from extended class (or the enclosing class)
fun <T : Any> T.logger(): Logger {
    return logger(this.javaClass)
}

/**
 *  return a lazy logger property delegate for enclosing class
 */
fun <R : Any> R.lazyLogger(): Lazy<Logger> {
    return lazy { logger(this.javaClass) }
}

/**
 * return a [Logger] property delegate for enclosing class
 */
fun <R : Any> R.injectLogger(): Lazy<Logger> {
    return lazyOf(logger(this.javaClass))
}

/**
 * marker interface and related extension (remove extension for Any.logger() in favour of this)
 */
interface Loggable

fun Loggable.logger(): Logger = logger(this.javaClass)
