package fr.tikione.c2e.core

import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class Tools {

    companion object {

        const val PAUSE_BETWEEN_MAG_DL = 30L
        const val VERSION_URL = "https://raw.githubusercontent.com/jonathanlermitage/tikione-c2e/master/uc/latest_version.txt"
        val VERSION = resourceAsStr("version.txt")

        var debug = false

        private val log: Logger = LoggerFactory.getLogger(Tools::class.java.javaClass)

        @Suppress("UNUSED_PARAMETER")
        @Throws(IOException::class)
        @JvmStatic
        fun fileAsBase64(file: File): String =
                Base64.encodeBase64String(file.readBytes())

        @Suppress("UNUSED_PARAMETER")
        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsBase64(path: String): String =
                Base64.encodeBase64String(IOUtils.toByteArray(Tools::class.java.classLoader.getResourceAsStream(path)))

        @Suppress("UNUSED_PARAMETER")
        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsStr(path: String): String =
                IOUtils.toString(Tools::class.java.classLoader.getResourceAsStream(path), StandardCharsets.UTF_8)

        @Throws(IOException::class)
        @JvmStatic
        fun readRemoteToBase64(url: String?): String {
            return Base64.encodeBase64String(URL(url).readBytes())
        }

        /**
         * Resize a picture.
         * Based on ImageMagick (must be in path) and validated with ImageMagick-7.0.6-10-Q16-x64 on Windows-8.1-x64.
         * We could resize via pure-Java, but it fails to handle some pictures: it adds something like a (horrible) grey
         * or pink filter. ImageMagick does a better job.
         *
         * @param src original picture.
         * @param dest new picture.
         * @param resize new size ratio (percents), eg. '50'.
         * @return convert (ImageMagick) exit value.
         */
        @JvmStatic
        fun resizePicture(src: String, dest: String, resize: String): Int {
            val cmd = "magick convert -resize $resize% $src $dest"
            val p = Runtime.getRuntime().exec(cmd)
            p.waitFor(30, TimeUnit.SECONDS)
            val ev = p.exitValue()
            if (ev != 0) {
                log.warn("la commande ImageMagick [$cmd] s'est teminee ave le code [$ev]")
            }
            return ev
        }

        @JvmStatic
        fun setProxy(host: String, port: String) {
            log.info("utilisation du proxy HTTP(S) {}:{}", host, port)
            System.setProperty("http.proxyHost", host)
            System.setProperty("http.proxyPort", port)
            System.setProperty("https.proxyHost", host)
            System.setProperty("https.proxyPort", port)
        }

        @JvmStatic
        fun setSysProxy() {
            log.info("utilisation du proxy systeme")
            System.setProperty("java.net.useSystemProxies", "true")
        }
    }
}
