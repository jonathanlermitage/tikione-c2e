package fr.tikione.c2e.service

import android.content.res.AssetManager
import compat.Tools
import fr.tikione.c2e.service.web.AbstractReader
import java.io.IOException
import java.text.Normalizer
import java.text.Normalizer.Form.NFD

abstract class AbstractWriter(private var asset: AssetManager) {

    fun filled(str: String?): Boolean {
        return str != null && !str.isEmpty()
    }

    fun normalizeAnchorUrl(str: String): String {
        return Normalizer.normalize(str, NFD)
                .replace("\"".toRegex(), "")
                .replace("'".toRegex(), "")
                .replace("\\s".toRegex(), "")
    }

    fun fastEquals(s1: String, s2: String): Boolean {
        if (s1.length != s2.length) {
            return false
        } else if (s1.isEmpty()) {
            return true
        }
        val portionSize = if (s1.length > 30) 30 else s1.length
        return s1.substring(0, portionSize) == s2.substring(0, portionSize)
    }

    @Throws(IOException::class)
    fun resourceAsBase64(path: String): String {
        return Tools.resourceAsBase64(path, asset)
    }

    @Throws(IOException::class)
    fun resourceAsStr(path: String): String {
        return Tools.resourceAsStr(path, asset)
    }

    fun richToHtml(rich: String): String {
        return rich.replace(AbstractReader.CUSTOMTAG_EM_START.toRegex(), " <em> ")
                .replace(AbstractReader.CUSTOMTAG_EM_END.toRegex(), " </em> ")
                .replace(AbstractReader.CUSTOMTAG_STRONG_START.toRegex(), " <strong> ")
                .replace(AbstractReader.CUSTOMTAG_STRONG_END.toRegex(), " </strong> ")
    }

    companion object {

        val EXT_LNK = "www"
    }
}
