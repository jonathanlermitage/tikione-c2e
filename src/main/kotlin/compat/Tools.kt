package compat

import android.content.res.AssetManager
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * Utility class that should be reworked in Android project to use standard
 * Android classes (Base64, classloader) instead of JDK or Apache Commons ones.
 */
class Tools {

    companion object {

        private val log: Logger = LoggerFactory.getLogger(Tools::class.java.javaClass)

        @Suppress("UNUSED_PARAMETER")
        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsBase64(path: String, asset: AssetManager): String {
            return Base64.encodeBase64String(IOUtils.toByteArray(Tools::class.java.classLoader.getResourceAsStream(path)))
        }

        @Suppress("UNUSED_PARAMETER")
        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsStr(path: String, asset: AssetManager): String {
            return IOUtils.toString(Tools::class.java.classLoader.getResourceAsStream(path), StandardCharsets.UTF_8)
        }

        /**
         * Resize a picture.
         * Based on ImageMagick (must be in path) and validated with ImageMagick-7.0.6-10-Q16-x64 on Windows-8.1-x64.
         *
         * todo: why not using Java directly for this operation ?
         * see https://stackoverflow.com/questions/244164/how-can-i-resize-an-image-using-java
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
    }
}
