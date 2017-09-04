package compat

import android.content.res.AssetManager
import android.util.Base64
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
        fun resourceAsBase64(path: String, asset: AssetManager): String {
            return Base64.encodeToString(IOUtils.toByteArray(asset.open(path)), Base64.DEFAULT)
        }

        @Throws(IOException::class)
        @JvmStatic
        fun resourceAsStr(path: String, asset: AssetManager): String {
            return IOUtils.toString(asset.open(path), StandardCharsets.UTF_8)
        }

        @Suppress("unused", "UNUSED_PARAMETER")
        @JvmStatic
        fun resizePicture(src: String, dest: String, resize: String): Int {
            return 0
        }
    }
}
