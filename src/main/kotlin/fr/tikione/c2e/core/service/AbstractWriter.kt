package fr.tikione.c2e.core.service

import com.github.salomonbrys.kodein.instance
import compat.AssetService
import compat.Tools
import fr.tikione.c2e.core.coreKodein
import fr.tikione.c2e.core.service.web.AbstractReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.text.Normalizer
import java.text.Normalizer.Form.NFD

abstract class AbstractWriter {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val assetService: AssetService = coreKodein.instance()

    fun filled(str: String?): Boolean = !str.isNullOrBlank()

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
    fun resourceAsBase64(path: String): String = Tools.resourceAsBase64(path, assetService.getAssetManager())

    @Throws(IOException::class)
    fun resourceAsStr(path: String): String = Tools.resourceAsStr(path, assetService.getAssetManager())

    fun richToHtml(rich: String): String {
        return rich.replace(AbstractReader.CUSTOMTAG_EM_START.toRegex(), " <em> ")
                .replace(AbstractReader.CUSTOMTAG_EM_END.toRegex(), " </em> ")
                .replace(AbstractReader.CUSTOMTAG_STRONG_START.toRegex(), " <strong> ")
                .replace(AbstractReader.CUSTOMTAG_STRONG_END.toRegex(), " </strong> ")
    }

    fun findFontAsBase64(dysfont: Boolean): String {
        val ttfs = File(".").listFiles { _, name -> name.toUpperCase().endsWith(".TTF") }
        return if (ttfs.isEmpty()) {
            if (dysfont) resourceAsBase64("tmpl/html-export/style/OpenDyslexic2-Regular.ttf")
            else resourceAsBase64("tmpl/html-export/style/RobotoSlab-Light.ttf")

        } else {
            log.info("utilisation de la police de caracteres {}", ttfs[0].canonicalPath)
            Tools.fileAsBase64(ttfs[0], assetService.getAssetManager())
        }
    }

    companion object {
        const val EXT_LNK = "www"
    }
}
