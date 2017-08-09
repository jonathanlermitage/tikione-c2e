package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.model.web.Article
import fr.tikione.c2e.service.web.AbstractReader
import org.jsoup.nodes.Document
import java.util.*

abstract class AbstractScraper : AbstractReader(), CPCScraperService {

    fun rate(articles: List<Article>?): Int {
        return articles?.stream()?.mapToInt({ it.rate() })?.sum() ?: -1
    }

    override fun extractArticles(doc: Document): Map<Int, List<Article>> {
        val score = HashMap<Int, List<Article>>()

        try {
            val news = extractNews(doc)
            score.put(rate(news), news)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val singleNews = extractSingleNews(doc)
            score.put(rate(singleNews), singleNews)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val tests = extractTests(doc)
            score.put(rate(tests), tests)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val shortTests = extractShortTests(doc)
            score.put(rate(shortTests), shortTests)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val plumePudding = extractPlumePudding(doc)
            score.put(rate(plumePudding), plumePudding)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val coming = extractComing(doc)
            score.put(rate(coming), coming)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val underConstruction = extractUnderConstruction(doc)
            score.put(rate(underConstruction), underConstruction)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val techno = extractTechno(doc)
            score.put(rate(techno), techno)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val study = extractStudy(doc)
            score.put(rate(study), study)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val everythingElse = extractEverythingElse(doc)
            score.put(rate(everythingElse), everythingElse)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return score
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
