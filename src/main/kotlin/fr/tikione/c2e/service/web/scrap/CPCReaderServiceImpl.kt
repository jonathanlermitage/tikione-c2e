package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.Main
import fr.tikione.c2e.model.web.*
import fr.tikione.c2e.service.web.AbstractReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class CPCReaderServiceImpl : AbstractReader(), CPCReaderService {

    var log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val cpcScraperService = CPCScraperServiceImpl()

    override fun listDownloadableMagazines(auth: Auth): List<Int> {
        val doc = Jsoup.connect(CPC_BASE_URL)
                .cookies(auth.cookies)
                .userAgent(AbstractReader.UA)
                .get()
        val archives = doc.getElementsByClass("archive")
        val magNumers = ArrayList<Int>()
        archives.forEach { element ->
            var n = -1
            val sn = element.getElementsByTag("a").attr("href").substring("/numero/".length)
            try {
                n = Integer.parseInt(sn)
            } catch (nfe: NumberFormatException) {
                log.debug("un magazine a un numéro invalide, il sera ignoré : {}", sn)
            }

            if (n >= 348) { // 348 is the first digitalized magazine
                magNumers.add(n)
            }
        }
        magNumers.add(magNumers[0] + 1)
        magNumers.sortDescending()
        return magNumers
    }

    override fun downloadMagazine(auth: Auth, number: Int): Magazine {
        log.info("téléchargement du numéro {}...", number)
        val doc = queryUrl(auth, CPC_MAG_NUMBER_BASE_URL.replace("_NUM_", Integer.toString(number)))
        val mag = Magazine()
        mag.number = number
        mag.title = doc.getElementById("numero-titre").text()
        mag.login = auth.login
        mag.edito = extractEdito(doc)
        mag.toc = extractToc(auth, doc)
        return mag
    }

    private fun extractEdito(doc: Document): Edito {
        val edito = Edito()
        val container = doc.getElementById("block-edito-content")
        edito.authorAndDate = container.getElementById("numero-auteur-date").text()
        edito.title = container.getElementById("numero-titre").text()
        edito.content = container.getElementById("numero-edito").text()
        return edito
    }

    private fun extractToc(auth: Auth, doc: Document): ArrayList<TocCategory> {
        val container = doc.getElementById("block-numerosommaire")
        val columns = container.getElementsByClass("columns")
        val toc = columns.mapTo(ArrayList<TocCategory>()) { buildTocItem(auth, it) }
        return toc
    }

    private fun buildTocItem(auth: Auth, elt: Element): TocCategory {
        val tocCategory = TocCategory()
        val title = clean(elt.getElementsByTag("h3")[0].text())
        tocCategory.title = title
        elt.getElementsByTag("article").forEach { sheet ->
            tocCategory.items.add(TocItem(
                    sheet.text(),
                    CPC_BASE_URL + attr(sheet.getElementsByTag("a"), "href"),
                    extractArticles(auth, CPC_BASE_URL + attr(sheet.getElementsByTag("a"), "href"))))
        }
        return tocCategory
    }

    private fun extractArticles(auth: Auth, url: String): List<Article>? {
        log.info("récupération de l'article {}", url)
        TimeUnit.MILLISECONDS.sleep(500) // be nice with CanardPC website
        val doc = queryUrl(auth, url)
        val articles = cpcScraperService.extractBestArticles(doc)
        if (Main.DEBUG) {
            articles.forEach { article -> log.debug(article.toString()) }
        }
        return articles
    }
}
