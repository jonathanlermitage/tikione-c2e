package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.model.web.Article
import org.jsoup.nodes.Document

interface CPCScraperService {

    /** Extract web page articles and associate this operation to a general relevance score.  */
    fun extractArticles(doc: Document): Map<Int, List<Article>>

    fun extractSingleNews(doc: Document): List<Article>

    fun extractNews(doc: Document): List<Article>

    fun extractTests(doc: Document): List<Article>

    fun extractShortTests(doc: Document): List<Article>

    fun extractPlumePudding(doc: Document): List<Article>

    fun extractComing(doc: Document): List<Article>

    fun extractUnderConstruction(doc: Document): List<Article>

    fun extractTechno(doc: Document): List<Article>

    fun extractStudy(doc: Document): List<Article>

    fun extractEverythingElse(doc: Document): List<Article>
}
