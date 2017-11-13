package fr.tikione.c2e.service.html

import android.content.res.AssetManager
import compat.Tools
import fr.tikione.c2e.Main
import fr.tikione.c2e.model.web.Article
import fr.tikione.c2e.model.web.ArticleType
import fr.tikione.c2e.model.web.Edito
import fr.tikione.c2e.model.web.Magazine
import fr.tikione.c2e.service.AbstractWriter
import net.sf.jmimemagic.Magic
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FileUtils.ONE_KB
import org.apache.commons.io.FileUtils.ONE_MB
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class HtmlWriterServiceImpl(asset: AssetManager) : AbstractWriter(asset), HtmlWriterService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(IOException::class)
    override fun write(magazine: Magazine, file: File, incluePictures: Boolean, resize: String?) {
        file.delete()
        if (file.exists()) {
            throw IOException("impossible d'ecraser le fichier : " + file.absolutePath)
        }
        val faviconBase64 = resourceAsBase64("tmpl/html-export/img/french_duck.png")
        val fontRobotoBase64 = resourceAsBase64("tmpl/html-export/style/RobotoSlab-Light.ttf")
        val cssDay = resourceAsStr("tmpl/html-export/style/day.css")
                .replace("$\$robotoFont_base64$$", fontRobotoBase64)
        val cssNight = resourceAsStr("tmpl/html-export/style/night.css")
        val js = resourceAsStr("tmpl/html-export/main.js")
        val header = resourceAsStr("tmpl/html-export/header.html")
                .replace("$\$login$$", magazine.login!!)
                .replace("$\$version$$", Main.VERSION)
                .replace("$\$timestamp$$", Date().toString())
                .replace("$\$mag_number$$", magazine.number)
                .replace("$\$favicon_base64$$", faviconBase64)
                .replace("/*$\$css_day$$*/", cssDay)
                .replace("/*$\$css_night$$*/", cssNight)
                .replace("/*$\$js$$*/", js)
        val footer = resourceAsStr("tmpl/html-export/footer.html")

        BufferedWriter(FileWriter(file)).use { w ->
            w.write(header)

            // toc
            w.write("<div id='toc'>\n")
            w.write("<h1 class='toc-title'>Sommaire CanardPC n°" + magazine.number + "</h1>\n")
            w.write("<div class='toc-columns-container'>\n")

            // Edito is apart, prepend it as the first item in the TOC
            w.write(elm("h3", "toc-item-title",
                a("", mapOf<String?, String?>(
                        "href" to "#Edito",
                        "onclick" to "showToc(false);"),
                        normalizeAnchorUrl("Edito")
                )
            ))


            // now create the links for all the categories and articles
            for (category in magazine.toc) {
                w.write("<h2 class='toc-category-title'>" + category.title + "</h2>\n\n")
                for (tocItem in category.items) {
                    w.write("<h3 class='toc-item-title'><a href='#"
                            + normalizeAnchorUrl(category.title!! + tocItem.title!!) + "' "
                            + " onclick='showToc(false);'>" + tocItem.title + "</a> "
                            + "<a class='toc-ext-lnk' href='" + tocItem.url + "' target='_blank' title='Vers le site CanardPC - nouvelle page'>"
                            + AbstractWriter.EXT_LNK
                            + "</a></h3>\n\n")
                }
            }
            w.write("</div>\n")
            val funpicDay = resourceAsBase64("tmpl/html-export/img/canard_ouais-day.png")
            val funpicNight = resourceAsBase64("tmpl/html-export/img/canard_ouais-night.png")
            w.write(resourceAsStr("tmpl/html-export/toc-footer.html")
                    .replace("$\$funpicDay_base64$$", funpicDay)
                    .replace("$\$funpicNight_base64$$", funpicNight))
            w.write("<br/><br/><br/></div>\n")
            w.write("<div id='articles'>\n")


            writeEdito(w, magazine.edito)

            // articles
            for (category in magazine.toc) {
                w.write("<h2 class=\"category-title\">" + category.title + "</h2>\n\n")
                for (tocItem in category.items) {
                    w.write("<div id='"
                            + normalizeAnchorUrl(category.title!! + tocItem.title!!)
                            + "'class=\"article-title\">"
                            + tocItem.title
                            + " <a class='toc-ext-lnk article-ext-lnk' href='" + tocItem.url + "' target='_blank' title='Vers le site CanardPC - nouvelle page'>"
                            + AbstractWriter.EXT_LNK
                            + "</a></div>\n\n")
                    tocItem.articles!!.forEach { article -> writeArticle(w, article, incluePictures, resize) }
                }
            }
            w.write("</div>\n")

            w.write(footer)
        }
        val fileSize = FileUtils.sizeOf(file)
        val sizeInMb = fileSize > ONE_MB
        log.info("fichier HTML cree : {} (environ {}{})",
                file.absolutePath,
                if (sizeInMb) fileSize / ONE_MB else fileSize / ONE_KB,
                if (sizeInMb) "MB" else "KB")
    }

    private fun writeEdito(w: Writer, edito: Edito?) {
        w.write( div("edito-cotainer", mapOf<String?, String?>("id" to normalizeAnchorUrl("Edito")),
                div("article-title", edito?.title)
                + div("article",
                    div("article-author-creationdate", edito?.authorAndDate),
                    div("article-content", edito?.content)
                )
        )
        )
    }

    private fun writeArticle(w: Writer, article: Article, incluePictures: Boolean, resize: String?) {
        w.write("\n<!--article.getType()=" + article.type + "-->\n\n")
        if (ArticleType.NEWS === article.type) {
            w.write("<div class='news'>\n")
            if (filled(article.category)) {
                w.write("<div class='news-category'>" + article.category + "</div>\n")
            }
            if (filled(article.title)) {
                w.write("<div class='news-title'>" + article.title + "</div>\n")
            }
            writeArticleAuthorCreationdate(w, article)
            writeArticleContents(w, article)
        } else {
            w.write("<div class='article'>\n")
            writeArticleSpecs(w, article)
            writeArticleSubtitle(w, article)
            writeArticleHeaderContent(w, article)
            writeArticleAuthorCreationdate(w, article)
            writeArticleContents(w, article)
            if (incluePictures) {
                writeArticlePictures(w, article, resize)
            }
            writeArticleOpinion(w, article)
            writeArticleState(w, article)
        }
        writeArticleLinks(w, article)
        w.write("</div>\n")
    }

    private fun writeArticleSubtitle(w: Writer, article: Article) {
        if (filled(article.subtitle)) {
            w.write("<div class='article-subtitle'>")
            w.write(article.subtitle!!)
            w.write("</div>\n")
        }
    }

    private fun writeArticleAuthorCreationdate(w: Writer, article: Article) {
        val content = ArrayList<String>()
        
        if(!article.author.isNullOrBlank()) {
            content.add("Par ${article.author}")
        }
        
        if(article.date != null) {
            content.add("${if(content.isEmpty()) "Le " else "le "} ${article.getFormattedDate()}")
        }
        
        w.write(div("article-author-creationdate", content.joinToString(separator = " | ")))
    }

 

    private fun writeArticleSpecs(w: Writer, article: Article) {
        val buff = StringBuilder()
        buff.append("<div class='article-specs'>\n")
        var contentFilled = false
        for (spec in Arrays.asList<String>(
                article.gameDev,
                article.gameNature,
                article.gameEditor,
                article.gamePlatform,
                article.gameTester,
                article.gameConfig,
                article.gameDDL,
                article.gameLang,
                article.gameDRM)) {
            if (filled(spec)) {
                buff.append("<span class='article-specs-item'>// ").append(boldSpecTitle(spec)).append("</span> \n")
                contentFilled = true
            }
        }
        buff.append("</div>\n")
        if (contentFilled) {
            w.write(buff.toString())
        }
    }

    private fun writeArticleHeaderContent(w: Writer, article: Article) {
        if (filled(article.headerContent)) {
            w.write("<div class='article-headercontent'>" + article.headerContent?.let { richToHtml(it) } + "</div>\n")
        }
    }

    private fun writeArticleContents(w: Writer, article: Article) {
        for (content in article.contents) {
            if (!content.text!!.isEmpty()) {
                var cssClass = "article-content"
                for (encadre in article.encadreContents) {
                    if (fastEquals(content.text as String, encadre)) {
                        cssClass = "article-encadre"
                        break
                    }
                }
                w.write("<p class=\"" + cssClass + "\">" + richToHtml(content.text as String) + "</p>\n")
            }
        }
    }

    private fun writeArticleLinks(w: Writer, article: Article) {
        if (!article.gameLinks.isEmpty()) {
            val lnksTitle = if (filled(article.gameLinkTitle)) article.gameLinkTitle else "Liens externes"
            w.write("<div class='article-gamelink-title'>$lnksTitle</div>\n")
            for (lnk in article.gameLinks) {
                var lnkText = lnk.replace("http://".toRegex(), "").replace("https://".toRegex(), "")
                if (lnkText.length > 50) {
                    lnkText = lnkText.substring(0, 47) + "..."
                }
                w.write("<div class='article-gamelink-title-lnk'><a class='article-gamelink-title-lnk' target='_blank' href='$lnk'>$lnkText</a></div>\n")
            }
        }
    }

    private fun writeArticlePictures(w: Writer, article: Article, resize: String?) {
        var hasPictures = false
        for (picture in article.pictures) {
            if (picture.url != null && !picture.url!!.trim { it <= ' ' }.isEmpty()) {
                hasPictures = true
                break
            }
        }
        if (hasPictures) {
            w.write("<div class='article-pictures'>\n")
            w.write("<div class='article-pictures-tip'>Images : cliquez/tapez sur une image pour l'agrandir, recommencez pour la reduire.</div>\n")
            for (picture in article.pictures) {
                if (picture.url != null && !picture.url!!.isEmpty()) {
                    log.info("recuperation de l'image {}", picture.url as String)
                    var picBytes = IOUtils.toByteArray(URL(picture.url!!))
                    TimeUnit.MILLISECONDS.sleep(250) // be nice with CanardPC website
                    val magicmatch = Magic.getMagicMatch(picBytes)
                    var ext = magicmatch.extension

                    if (resize != null && !resize.isBlank()) {
                        val tmpSrc = "c2e.src.tmp.$ext"
                        val tmpDest = "c2e.dest.tmp.jpg"
                        val tmpSrcFile = File(tmpSrc)
                        val tmpDestFile = File(tmpDest)
                        tmpSrcFile.delete()
                        tmpDestFile.delete()
                        FileUtils.writeByteArrayToFile(File(tmpSrc), picBytes)
                        Tools.resizePicture(tmpSrc, tmpDest, resize.toString())
                        if (tmpDestFile.exists()) {
                            ext = "jpeg"
                            picBytes = FileUtils.readFileToByteArray(tmpDestFile)
                        }
                        tmpSrcFile.delete()
                        tmpDestFile.delete()
                    }

                    val picB64 = Base64.encodeBase64String(picBytes)
                    val pictureId = Base64.encodeBase64String(picture.url!!.toByteArray())
                            .replace("=".toRegex(), "")
                            .replace(",".toRegex(), "")
                    val pictureBoxId = pictureId + "box"
                    w.write("<div class='article-picture-box' id='$pictureBoxId'>\n")
                    w.write("<span class='article-picture'><img src='data:image/" + ext + ";base64," + picB64 +
                            "' id='" + pictureId + "' " +
                            " onclick=\"showPicture('" + pictureId + "', '" + pictureBoxId + "');\" /></span>\n")
                    if (picture.legend != null && !picture.legend!!.isEmpty()) {
                        w.write("<span class='article-picture-legend'>" + picture.legend + "</span>")
                    }
                    w.write("</div>\n")
                }
            }
            w.write("</div>\n")
        }
    }

    private fun writeArticleOpinion(w: Writer, article: Article) {
        val buff = StringBuilder()
        buff.append("<div class='article-opinion'>\n")
        var contentFilled = false
        if (filled(article.gameOpinionTitle)) {
            buff.append("<div class='article-opinion-title'>").append(article.gameOpinionTitle).append("</div>\n")
            contentFilled = true
        }
        if (filled(article.gameOpinion)) {
            buff.append("<div class='article-opinion-content'>").append(article.gameOpinion).append("</div>\n")
            contentFilled = true
        }
        if (filled(article.gameScoreText)) {
            buff.append("<div class='article-opinion-score-text'>").append(article.gameScoreText).append("</span></div>\n")
            contentFilled = true
        }
        if (filled(article.gameScore)) {
            buff.append("<div class='article-opinion-score'>\n")
            if (filled(article.gameScore)) {
                var score: String? = article.gameScore
                if (score?.startsWith("0.") as Boolean) {
                    score = score.substring(2)
                }
                buff.append("<span class='article-opinion-score-number'>").append(score).append("</span>")
            }
            buff.append("</div>\n")
            contentFilled = true
        }
        buff.append("</div>\n")
        if (contentFilled) {
            w.write(buff.toString())
        }
    }

    private fun writeArticleState(w: Writer, article: Article) {
        val buff = StringBuilder()
        buff.append("<div class='article-state'>\n")
        var contentFilled = false
        if (filled(article.gameStateTitle)) {
            buff.append("<div class='article-state-title'>").append(article.gameStateTitle).append("</div>\n")
            contentFilled = true
        }
        if (filled(article.gameState)) {
            buff.append("<div class='article-state-content'>").append(article.gameState).append("</div>\n")
            contentFilled = true
        }
        if (filled(article.gameAdviceTitle) && filled(article.gameAdvice)) {
            buff.append("<div class='article-state-score-value'>").append(article.gameAdviceTitle).append(" ")
                    .append(article.gameAdvice).append("</span></div>\n")
            contentFilled = true
        }
        buff.append("</div>\n")
        if (contentFilled) {
            w.write(buff.toString())
        }
    }

    private fun boldSpecTitle(str: String): String {
        return if (str.contains(":")) "<strong>" + str.substring(0, str.indexOf(":")) + "</strong> : " + str.substring(1 + str.indexOf(":")) else str
    }


    /**
     * return a 'a' element with the given class, attributes and contents
     */
    private fun a(cssClass: String? , attributes: Map<String?, String?>, vararg contents: String?) : String {
        return elmWithAttr("a", cssClass, attributes, contents.asList())
    }

    /**
     * return a div element with the given class, attributes and contents
     */
    private fun div(cssClass: String? , attributes: Map<String?, String?> = emptyMap(), vararg contents: String?) : String {
        return elmWithAttr("div", cssClass, attributes, contents.asList())
    }

    /**
     * return a div element with the given class and contents
     */
    private fun div(cssClass: String? , vararg contents: String?) : String {
        return elmWithAttr("div", cssClass,  mapOf("class" to cssClass), contents.asList())
    }

    /**
     * return an element with the given class and contents
     */
    private fun elm(name : String, cssClass: String?, vararg contents: String?) : String {
        return elmWithAttr(name, cssClass, mapOf("class" to cssClass), contents.asList())
    }


    /**
     * Simple generic way to produce an html 'tag' with attributes and content
     */
    private fun elmWithAttr(name : String, cssClass : String?, attributes : Map<String?, String?>, contents : List<String?>) : String {
        if(contents.isEmpty())
            return ""

        val elmCContent = contents.filterNotNull().joinToString("\n")

        val htmlAttributesMap = LinkedHashMap<String?, String?>(attributes.size+1)
        htmlAttributesMap["class"]=cssClass

        htmlAttributesMap.putAll(attributes
                //rules: an empty key is discarded, but an empty value is kept. To withdraw a value it must be null
                .filterKeys { !it.isNullOrBlank() }
                .filterValues { it != null }
        )

        @Suppress("SimplifiableCallChain")
        val htmlAttributes = htmlAttributesMap
                .map { it -> "${it.key}='${it.value}'" }
                .joinToString(" ")


        return """
            <$name $htmlAttributes>
                $elmCContent
            </$name>
            """.trimIndent()
    }
}
