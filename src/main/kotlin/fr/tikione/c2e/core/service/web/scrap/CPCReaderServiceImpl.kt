package fr.tikione.c2e.core.service.web.scrap

import com.github.salomonbrys.kodein.instance
import compat.Tools
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.model.web.*
import fr.tikione.c2e.core.service.web.AbstractReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class CPCReaderServiceImpl : AbstractReader(), CPCReaderService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val cpcScraperService: CPCScraperService = kodein.instance()

    override fun listDownloadableMagazines(auth: Auth): ArrayList<String> {
        val doc = Jsoup.connect(CPC_BASE_URL)
                .cookies(auth.cookies)
                .userAgent(AbstractReader.UA)
                .get()
        val archives = doc.getElementsByClass("archive")
        val magNumers = ArrayList<String>()
        archives.forEach { element ->
            magNumers.add(element.getElementsByTag("a").attr("href").substring("/numero/".length))
        }
        try {
            magNumers.add(Integer.toString(Integer.parseInt(magNumers[0]) + 1))
        } catch (e: Exception) {
            log.debug("erreur lors du listing des numeros disponibles ('{}' n'est pas un nombre entier)", magNumers[0])
        }
        magNumers.sortDescending()
        return magNumers
    }

    override fun extractAuthorsPicture(doc: Document): Map<String, AuthorPicture> {
        val authorsAndPic = HashMap<String, AuthorPicture>()
        try {
            doc.getElementsByClass("lequipe")[0].getElementsByTag("td").forEach { elt ->
                val realName: String = text(elt.getElementsByTag("h3"))!!
                if (realName.isNotEmpty() && !authorsAndPic.containsKey(realName.toUpperCase())) {
                    val picture = Tools.readRemoteToBase64(CPC_BASE_URL + elt.getElementsByTag("img").attr("src"))
                    authorsAndPic.put(realName.toUpperCase(), AuthorPicture(realName, picture))

                    // Some authors have different names in About-page and articles: register both
                    if (realName.equals("Louis-Ferdinand Sébum", true)) {
                        authorsAndPic.put("L-F. Sébum".toUpperCase(), AuthorPicture(realName, picture))
                    }
                }
            }
        } catch (e: Exception) {
            log.warn("impossible de recuperer les pictos des redacteurs, poursuite du telechargement", e)
        }
        return authorsAndPic
    }

    override fun downloadMagazine(auth: Auth, number: String): Magazine {
        log.info("telechargement du numero {}...", number)
        val doc = queryUrl(auth, CPC_MAG_NUMBER_BASE_URL.replace("_NUM_", number))
        val mag = Magazine()
        mag.number = number
        mag.title = doc.getElementById("numero-titre").text()
        mag.login = auth.login
        mag.edito = extractEdito(doc)
        mag.toc = extractToc(auth, doc)

        // Décision de la rédac CanardCPC : ne pas intégrer ed picto du site CanardPC
        mag.authorsPicture = Collections.emptyMap() //extractAuthorsPicture(queryUrl(auth, CPC_AUTHORS_URL))

        return mag
    }

    private fun extractEdito(doc: Document): Edito {
        val edito = Edito()
        val container = doc.getElementById("block-edito-content")
        edito.authorAndDate = container.getElementById("numero-auteur-date").text()
        edito.title = container.getElementById("numero-titre").text()
        edito.content = container.getElementById("numero-edito").text()
        edito.coverUrl = doc.getElementById("numero-couverture")?.attr("src")
        if (edito.coverUrl != null) {
            edito.coverUrl = CPC_BASE_URL + edito.coverUrl
        }
        return edito
    }

    private fun extractToc(auth: Auth, doc: Document): ArrayList<TocCategory> {
        val container = doc.getElementById("block-numerosommaire")
        val columns = container.getElementsByClass("columns")
        val tocCategories = columns.mapTo(ArrayList()) { buildTocItem(auth, it) }

        // Fix https://github.com/jonathanlermitage/tikione-c2e/issues/27
        val fixedTocCategories = ArrayList<TocCategory>()
        for (tocCategory in tocCategories) {
            if (tocCategory.title.isNullOrEmpty() && !fixedTocCategories.isEmpty()) {
                fixedTocCategories.get(fixedTocCategories.size - 1).items.addAll(tocCategory.items)
            } else {
                fixedTocCategories.add(tocCategory)
            }
        }

        return fixedTocCategories

    }

    private fun buildTocItem(auth: Auth, elt: Element): TocCategory {
        val tocCategory = TocCategory()
        val titleElt = elt.getElementsByTag("h3")
        if (titleElt.size == 0) {
            // Fix https://github.com/jonathanlermitage/tikione-c2e/issues/27
            // La ToC du numéro 374 est mal formée : la div "Tests brefs" ne contient pas tous les éléments de la rubrique
            tocCategory.title = ""
        } else {
            val title = clean(elt.getElementsByTag("h3")[0].text())
            tocCategory.title = title
        }
        elt.getElementsByTag("article").forEach { sheet ->
            tocCategory.items.add(TocItem(
                    sheet.text(),
                    CPC_BASE_URL + attr(sheet.getElementsByTag("a"), "href"),
                    extractArticles(auth, CPC_BASE_URL + attr(sheet.getElementsByTag("a"), "href"))))
        }
        return tocCategory
    }

    private fun extractArticles(auth: Auth, url: String): List<Article>? {
        log.info("recuperation de l'article {}", url)
        TimeUnit.MILLISECONDS.sleep(500) // be nice with CanardPC website
        val doc = queryUrl(auth, url)
        val articles = cpcScraperService.extractBestArticles(doc)
        if (Tools.debug) {
            articles.forEach { article -> log.debug(article.toString()) }
        }
        return articles
    }
}
