package fr.tikione.c2e.model.web

/**
 * CanardPC web.
 */
class Magazine {

    var number: Int = 0
    var title: String? = null
    var login: String? = null
    var edito: Edito? = null
    var toc = ArrayList<TocCategory>()

    constructor() {
    }

    constructor(number: Int, title: String?, login: String?, edito: Edito?, toc: ArrayList<TocCategory>) {
        this.number = number
        this.title = title
        this.login = login
        this.edito = edito
        this.toc = toc
    }
}
