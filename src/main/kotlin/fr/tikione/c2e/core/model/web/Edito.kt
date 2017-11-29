package fr.tikione.c2e.core.model.web

/**
 * Edito.
 */
class Edito {

    var title: String? = null
    var authorAndDate: String? = null
    var content: String? = null
    var coverUrl: String? = null

    constructor() {
    }

    constructor(title: String?, authorAndDate: String?, content: String?, coverUrl: String?) {
        this.title = title
        this.authorAndDate = authorAndDate
        this.content = content
        this.coverUrl = coverUrl
    }

    override fun toString(): String {
        return "Edito(title=$title, authorAndDate=$authorAndDate, content=$content, coverUrl=$coverUrl)"
    }
}
