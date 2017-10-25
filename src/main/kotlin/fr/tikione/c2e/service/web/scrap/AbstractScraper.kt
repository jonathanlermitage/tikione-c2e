package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.model.web.Article
import fr.tikione.c2e.service.web.AbstractReader
import org.jsoup.nodes.Document

abstract class AbstractScraper : AbstractReader(), CPCScraperService {

    private fun rate(articles: List<Article>?): Int {
        if (articles == null) {
            return -1
        }
        return articles.sumBy { it.rate() }
    }



    override fun extractBestArticles(doc: Document): List<Article> {
        val extractions = listOf(
                noException { extractNews(doc) },
                noException { extractSingleNews(doc) },
                noException { extractPlumePudding(doc) },
                noException { extractShortTests(doc) },
                noException { extractTests(doc) },
                noException { extractComing(doc) },
                noException { extractUnderConstruction(doc) },
                noException { extractTechno(doc) },
                noException { extractStudy(doc) },
                noException { extractEverythingElse(doc) }
        )

        return extractions.map { Pair(rate(it), it) }
                //sort by highest score (first) and return the articles (second)
                .sortedByDescending { it.first }.map { it.second }[0]
    }


    private inline fun noException(extrractor: () -> List<Article>): List<Article> {
        return try {
            extrractor()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun extractSingleNews(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractNews(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractTests(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractShortTests(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractPlumePudding(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractComing(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractUnderConstruction(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractTechno(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractStudy(doc: Document): List<Article> {
        return emptyList()
    }

    override fun extractEverythingElse(doc: Document): List<Article> {
        return emptyList()
    }
}
