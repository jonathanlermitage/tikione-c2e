package compat

import android.content.res.AssetManager
import android.util.Base64
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Utility class that should be reworked in Android project to use standard
 * Android classes (Base64, classloader) instead of JDK or Apache Commons ones.
 */
class Tools {

    companion object {

        const val VERSION = "android-1.0.0_c2ecore-1.3.10"
        var debug = false

        @Suppress("UNUSED_PARAMETER")
        @Throws(IOException::class)
        @JvmStatic
        fun fileAsBase64(file: File, asset: AssetManager?): String =
                Base64.encodeToString(IOUtils.toByteArray(file.toURI()), Base64.DEFAULT)

        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsBase64(path: String, asset: AssetManager?): String =
                Base64.encodeToString(IOUtils.toByteArray(asset?.open(path)), Base64.DEFAULT)

        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsStr(path: String, asset: AssetManager?): String =
                IOUtils.toString(asset?.open(path), StandardCharsets.UTF_8)

        @Throws(IOException::class)
        @JvmStatic
        fun readRemoteToBase64(url: String?): String {
            return Base64.encodeToString(IOUtils.toByteArray(URL(url)), Base64.DEFAULT)
        }

        @Throws(IOException::class)
        @JvmStatic
        fun byteArrayToBase64(byteArray: ByteArray): String {
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }


        @Suppress("unused", "UNUSED_PARAMETER")
        @JvmStatic
        fun resizePicture(src: String, dest: String, resize: String): Int = 0

        @JvmStatic
        fun isAndroid(): Boolean = (System.getProperty("java.vendor") == "The Android Project")

        @JvmStatic
        fun setProxy(host: String, port: String) {
            System.setProperty("http.proxyHost", host)
            System.setProperty("http.proxyPort", port)
            System.setProperty("https.proxyHost", host)
            System.setProperty("https.proxyPort", port)
        }

        @JvmStatic
        fun setSysProxy() {
            System.setProperty("java.net.useSystemProxies", "true")
        }
    }
}

class EndServiceException: Exception()