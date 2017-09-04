package fr.tikione.c2e.service.web.scrap

import fr.tikione.c2e.model.web.Article
import fr.tikione.c2e.service.web.AbstractReader
import org.jsoup.nodes.Document

abstract class AbstractScraper : AbstractReader(), CPCScraperService {

    fun rate(articles: List<Article>?): Int {
        if (articles == null) {
            return -1
        }
        return articles.sumBy { it.rate() }
    }

    override fun extractBestArticles(doc: Document): List<Article> {
        var score = 0
        var best: List<Article> = emptyList()

        try {
            val extract = extractNews(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractSingleNews(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractTests(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractShortTests(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractPlumePudding(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractComing(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractUnderConstruction(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractTechno(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractStudy(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
                score = rate
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extract = extractEverythingElse(doc)
            val rate = rate(extract)
            if (rate > score) {
                best = extract
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return best
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
