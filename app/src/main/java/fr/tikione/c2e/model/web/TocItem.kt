package fr.tikione.c2e.model.web

/**
 * TOC item.
 */
class TocItem {

    var title: String? = null
    var url: String? = null
    var articles: List<Article>? = null

    constructor() {
    }

    constructor(title: String?, url: String?, articles: List<Article>?) {
        this.title = title
        this.url = url
        this.articles = articles
    }

    override fun toString(): String {
        return "TocItem(title=$title, url=$url, articles=$articles)"
    }
}
