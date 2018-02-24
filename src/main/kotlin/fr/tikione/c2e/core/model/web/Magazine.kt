package fr.tikione.c2e.core.model.web

/**
 * CanardPC web magazine.
 */
class Magazine {

    var number: String = ""
    var title: String? = null
    var login: String? = null
    var edito: Edito? = null
    var toc = ArrayList<TocCategory>()
    var authorsPicture: Map<String, AuthorPicture> = HashMap()

    override fun toString(): String {
        return "Magazine(number=$number, title=$title, login=$login, edito=$edito, toc=$toc)"
    }
}
