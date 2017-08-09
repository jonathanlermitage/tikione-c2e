package fr.tikione.c2e.model.web

import java.util.*

/**
 * Represents an article, a game test, news, etc.
 */
class Article {

    var title: String? = null
    var subtitle: String? = null
    var authorAndDate: String? = null
    var category: String? = null
    var headerContent: String? = null
    var contents = ArrayList<Paragraph>()
    var encadreContents = ArrayList<String>()
    var pictures = ArrayList<Picture>()
    var wrappedArticles = ArrayList<Article>()
    var gameScore: String? = null
    var gameScoreText: String? = null
    var gameNature: String? = null
    var gameDev: String? = null
    var gameEditor: String? = null
    var gamePlatform: String? = null
    var gameTester: String? = null
    var gameConfig: String? = null
    var gameDDL: String? = null
    var gameLang: String? = null
    var gameDRM: String? = null
    var gameOpinionTitle: String? = null
    var gameOpinion: String? = null
    var gameLinkTitle: String? = null
    var gameLinks = ArrayList<String>()
    var gameAdviceTitle: String? = null
    var gameAdvice: String? = null
    var gamePrice: String? = null
    var gameStateTitle: String? = null
    var gameState: String? = null

    /** Scrapper used to fill this object. For debug purpose.  */
    var type: ArticleType? = null

    /**
     * Give a score to current object to estimate fidelity to real article.
     * Used to compare multiple extractions of the same article and keep the best one.
     * @return score.
     */
    fun rate(): Int {
        return toString().length
    }
}
