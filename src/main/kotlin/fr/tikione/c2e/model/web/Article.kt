package fr.tikione.c2e.model.web

import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents an article, a game test, news, etc.
 * todo: suggestion by game: why not using a data class instead of a class ? see: https://kotlinlang.org/docs/reference/data-classes.html
 */
class Article {

    var title: String? = null
    var subtitle: String? = null
    var author: String? = null
    var date: Date? = null
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

    override fun toString(): String {
        return "Article(title=$title, subtitle=$subtitle, author=$author, date=${getFormattedDate()}  category=$category, headerContent=$headerContent, contents=$contents, encadreContents=$encadreContents, pictures=$pictures, wrappedArticles=$wrappedArticles, gameScore=$gameScore, gameScoreText=$gameScoreText, gameNature=$gameNature, gameDev=$gameDev, gameEditor=$gameEditor, gamePlatform=$gamePlatform, gameTester=$gameTester, gameConfig=$gameConfig, gameDDL=$gameDDL, gameLang=$gameLang, gameDRM=$gameDRM, gameOpinionTitle=$gameOpinionTitle, gameOpinion=$gameOpinion, gameLinkTitle=$gameLinkTitle, gameLinks=$gameLinks, gameAdviceTitle=$gameAdviceTitle, gameAdvice=$gameAdvice, gamePrice=$gamePrice, gameStateTitle=$gameStateTitle, gameState=$gameState, type=$type)"
    }
    
    fun getFormattedDate(): String {
        if(date == null)
            return ""
        
        return SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(date)
    }
}
