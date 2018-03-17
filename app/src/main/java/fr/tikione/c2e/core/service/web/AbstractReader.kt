package fr.tikione.c2e.core.service.web

import fr.tikione.c2e.core.model.web.Auth
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.safety.Whitelist
import org.jsoup.select.Elements
import java.io.IOException

abstract class AbstractReader {

    /** Replace unbreakable spaces by regular spaces, and apply trim.  */
    fun clean(strToClean: String): String {
        var str = strToClean
        while (str.contains(160.toChar().toString())) {
            str = str.replace(160.toChar().toString(), " ")
        }
        var res = Jsoup.clean(str.trim { it <= ' ' }, Whitelist.none())
        if (res.toUpperCase().endsWith("TWITTER FACEBOOK EMAIL")) {
            res = res.substring(0, res.toUpperCase().lastIndexOf("TWITTER FACEBOOK EMAIL")) // remove social buttons
        }
        return res.trim { it <= ' ' }
    }

    /** Get a remote document.  */
    @Throws(IOException::class)
    fun queryUrl(auth: Auth, url: String): Document = Jsoup.connect(url)
            .cookies(auth.cookies)
            .userAgent(UA)
            .execute().parse()

    fun text(elt: Element?): String? = if (elt == null) null else clean(elt.text())

    /** Get text and keep some HTML styling tags: `em` and `strong`.  */
    fun richText(elt: Element?): String? {
        if (elt == null) {
            return null
        }
        var text = elt.html()
        var idx = 0
        while (text.indexOf(EM_START, idx) >= 0) {
            idx = text.indexOf(EM_START, idx) + EM_START.length
            val idxEnd = text.indexOf(EM_END, idx) + EM_END.length
            if (idx < idxEnd) {
                text = text.substring(0, idx) + CUSTOMTAG_EM_START + text.substring(idx, idxEnd) + CUSTOMTAG_EM_END + text.substring(idxEnd)
            }
        }
        idx = 0
        while (text.indexOf(STRONG_START, idx) >= 0) {
            idx = text.indexOf(STRONG_START, idx) + STRONG_START.length
            val idxEnd = text.indexOf(STRONG_END, idx) + STRONG_END.length
            if (idx < idxEnd) {
                text = text.substring(0, idx) + CUSTOMTAG_STRONG_START + text.substring(idx, idxEnd) + CUSTOMTAG_STRONG_END + text.substring(idxEnd)
            }
        }
        return clean(text)
    }

    fun text(vararg elt: Elements): String? {
        if (elt.isEmpty()) {
            return null
        }
        val buff = StringBuilder()
        elt.iterator().forEach { elements -> buff.append(clean(elements.text())) }
        return buff.toString()
    }

    fun attr(elt: Element?, attr: String): String? = elt?.attr(attr)

    fun attr(elt: Elements?, attr: String): String? = elt?.attr(attr)

    fun attr(baseUrl: String, elt: Elements?, attr: String): String? =
            if (elt?.attr(attr) == null || elt.attr(attr).trim { it <= ' ' }.isEmpty()) null else baseUrl + elt.attr(attr)

    companion object {
        val CPC_BASE_URL = "https://www.canardpc.com"
        val CPC_LOGIN_FORM_POST_URL = "$CPC_BASE_URL/user/login"
        val CPC_MAG_NUMBER_BASE_URL = "$CPC_BASE_URL/numero/_NUM_"
        val CPC_AUTHORS_URL = "$CPC_BASE_URL/qui-sommes-nous"
        val CUSTOMTAG_EM_START = "__c2e_emStart__"
        val CUSTOMTAG_EM_END = "__c2e_emEnd__"
        val CUSTOMTAG_STRONG_START = "__c2e_strongStart__"
        val CUSTOMTAG_STRONG_END = "__c2e_strongEnd__"
        private val EM_START = "<em>"
        private val EM_END = "</em>"
        private val STRONG_START = "<span class=\"title\">"
        private val STRONG_END = "</span>"

        /** UserAgent to include when scrapping CanardPC website. This is not mandatory, but a good practice.  */
        val UA = "fr.tikione.c2e"
    }
}
