package fr.tikione.c2e.model.web

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
}
