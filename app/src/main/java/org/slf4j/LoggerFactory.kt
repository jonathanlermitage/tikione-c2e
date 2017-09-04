package org.slf4j

@Suppress("UNUSED_PARAMETER", "unused")
object LoggerFactory {

    fun getLogger(o: Any): Logger {
        return Logger()
    }
}