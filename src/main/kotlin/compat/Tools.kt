package compat

import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Utility class that should be reworked in Android project to use standard
 * Android classes (Base64, classloader) instead of JDK or Apache Commons ones.
 */
class Tools {

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsBase64(path: String): String {
            return Base64.encodeBase64String(IOUtils.toByteArray(Tools::class.java.classLoader.getResourceAsStream(path)))
        }

        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsStr(path: String): String {
            return IOUtils.toString(Tools::class.java.classLoader.getResourceAsStream(path), StandardCharsets.UTF_8)
        }
    }
}
