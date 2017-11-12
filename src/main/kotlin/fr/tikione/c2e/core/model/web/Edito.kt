package fr.tikione.c2e.core.model.web

/**
 * Edito.
 */
class Edito {

    var title: String? = null
    var authorAndDate: String? = null
    var content: String? = null

    constructor() {
    }

    constructor(title: String?, authorAndDate: String?, content: String?) {
        this.title = title
        this.authorAndDate = authorAndDate
        this.content = content
    }

    override fun toString(): String {
        return "Edito(title=$title, authorAndDate=$authorAndDate, content=$content)"
    }
}
