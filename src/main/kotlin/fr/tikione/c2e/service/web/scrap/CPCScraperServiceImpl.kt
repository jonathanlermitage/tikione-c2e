package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.model.web.Article
import fr.tikione.c2e.model.web.ArticleType.*
import fr.tikione.c2e.model.web.Paragraph
import fr.tikione.c2e.model.web.Picture
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class CPCScraperServiceImpl : AbstractScraper(), CPCScraperService {

    override fun extractNews(doc: Document): List<Article> {
        val articles = ArrayList<Article>()
        if (doc.getElementsByClass("article-wrapper") != null) {
            for (elt in doc.getElementsByClass("article-wrapper")) {
                val article = Article()
                article.type = NEWS
                article.category = text(elt.getElementsByClass("categorie"))
                article.title = text(elt.getElementsByTag("h4"))
                var content: String? = text(elt.getElementsByClass("article-wrapper"))
                if (content != null && article.title != null && content.length > article.title.toString().length && article.title.toString().isNotEmpty()) {
                    // title and content are in the same div: fix content by removing title
                    content = content.substring(content.indexOf(article.title.toString()) + article.title.toString().length).trim { it <= ' ' }
                }
                article.contents.add(Paragraph(content))
                if (elt.getElementsByClass("singleImage") != null) {
                    article.pictures.add(Picture(attr(CPC_BASE_URL, elt.getElementsByClass("singleImage"), "href"), null))
                }
                extractLinks(article, elt)
                articles.add(article)
            }
        }
        return articles
    }

    override fun extractSingleNews(doc: Document): List<Article> {
        val article = Article()
        article.type = SINGLE_NEWS
        article.title = text(doc.getElementsByClass("article-title"))
        extractAuthorAndDate(doc, article)
        article.contents.add(Paragraph(text(doc.getElementsByClass("article-body"))))
        doc.getElementsByClass("article-images")
                .forEach { images ->
                    images.getElementsByClass("article-image")
                            .forEach { image ->
                                article.pictures.add(Picture(
                                        attr(CPC_BASE_URL, image.getElementsByTag("a"), "href"),
                                        text(image.getElementsByClass("article-image-legende"))
                                ))
                            }
                }
        extractLinks(article, doc)
        return listOf(article)
    }

    private fun CPCScraperServiceImpl.extractAuthorAndDate(doc: Document, article: Article) {
        val authorAndDate = text(doc.getElementsByClass("article-author"))
        article.author = extractAuthor(authorAndDate)
        article.date = extractDate(authorAndDate)
    }


    /**
     * CPC Format is  "Par Maria Kalash | le 29 août 2017"
     */
    fun extractDate(authorAndDate: String?): Date? {
        if(authorAndDate == null)
            return null

        val splittenAuthorAndDate = authorAndDate.toLowerCase().split("| le ")

        if (splittenAuthorAndDate.size != 2)
            return null

        val rawDate=splittenAuthorAndDate[1].trim()
        //ok, how trusty can we be with the CPC data format ? let's try a regular french parser
        return SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).parse(rawDate)
    }

    
    /**
     * CPC Format is  "Par Maria Kalash | le 29 août 2017"
     */
    fun extractAuthor(authorAndDate: String?): String? {
        if(authorAndDate == null)
            return null
        
        return "Par *(.*) *\\|.*".toRegex().matchEntire(authorAndDate)?.groups?.get(1)?.value?.trim()
        
    }

    override fun extractTests(doc: Document): List<Article> {
        val article = Article()
        article.type = TESTS
        article.title = text(doc.getElementsByClass("article-title"))
        article.subtitle = text(doc.getElementsByClass("article-subtitle"))
        extractAuthorAndDate(doc, article)
        article.headerContent = text(doc.getElementsByClass("article-chapo"))
        doc.getElementsByClass("article-body")
                .forEach { element -> article.contents.add(Paragraph(richText(element))) }
        doc.getElementsByAttributeValueMatching("class", "(article\\-intertitre$|article\\-encadre$)")
                .forEach { element -> article.contents.add(Paragraph(richText(element))) }
        doc.getElementsByClass("article-encadre")
                .forEach { element -> richText(element)?.let { article.encadreContents.add(it) } }
        article.gameScore = attr(doc.getElementById("article-note"), "data-pourcentage")
        article.gameScoreText = text(doc.getElementsByClass("article-note-prix"))
        article.gameNature = text(doc.getElementsByClass("game-genre"))
        article.gameDev = text(doc.getElementsByClass("game-dev"))
        article.gameEditor = text(doc.getElementsByClass("game-editor"))
        article.gamePlatform = text(doc.getElementsByClass("game-platform"))
        article.gameTester = text(doc.getElementsByClass("game-tester"))
        article.gameConfig = text(doc.getElementsByClass("game-config"))
        article.gameDDL = text(doc.getElementsByClass("game-ddl"))
        article.gameLang = text(doc.getElementsByClass("game-lang"))
        article.gameDRM = text(doc.getElementsByClass("game-drm"))
        article.gameOpinionTitle = text(doc.getElementsByClass("article-note-avis-titre"))
        article.gameOpinion = text(doc.getElementsByClass("article-note-avis"))
        article.gameAdviceTitle = text(doc.getElementsByClass("article-note-conseil-titre"))
        article.gameAdvice = text(doc.getElementsByClass("article-note-conseil"))
        article.gamePrice = text(doc.getElementsByClass("article-note-prix"))
        article.gameStateTitle = text(doc.getElementsByClass("article-note-etat-titre"))
        article.gameState = text(doc.getElementsByClass("article-note-etat"))
        article.gameOpinion = text(doc.getElementsByClass("article-note-avis"))
        extractLinks(article, doc)
        doc.getElementsByClass("article-images")
                .forEach { images ->
                    images.getElementsByClass("article-image")
                            .forEach { image ->
                                article.pictures.add(Picture(
                                        attr(CPC_BASE_URL, image.getElementsByTag("a"), "href"),
                                        text(image.getElementsByClass("article-image-legende"))
                                ))
                            }
                }
        if (doc.getElementsByClass("article-encadre-wrapper") != null) {
            for (elt in doc.getElementsByClass("article-encadre-wrapper")) {
                val wa = Article()
                wa.type = TESTS
                wa.title = text(elt.getElementsByClass("title"))
                wa.pictures.add(Picture(attr(CPC_BASE_URL, elt.getElementsByTag("a"), "href"), null))
                var content = text(elt.getElementsByClass("article-encadre"))
                if (wa.title != null) {
                    content = content?.substring(wa.title.toString().length)?.trim { it <= ' ' }
                }
                wa.contents.add(Paragraph(content))
                article.wrappedArticles.add(wa)
            }
        }
        return listOf(article)
    }

    private fun extractLinks(article: Article, elt: Element) {
        article.gameLinkTitle = text(elt.getElementsByAttributeValueMatching("class",
                "(article\\-lien\\-description$|article\\-liens\\-description$|article\\-liens\\-titre$)"))
        elt.getElementsByClass("article-lien-lien")
                .forEach { lnkDiv ->
                    lnkDiv.getElementsByTag("a")
                            .forEach { a -> article.gameLinks.add(a.attr("href")) }
                }
    }
}
