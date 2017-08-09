package fr.tikione.c2e.model.web

import java.util.*

/**
 * TOC category that contains [TocItem] elements.
 */
class TocCategory {

    var title: String? = null
    var items = ArrayList<TocItem>()

    constructor() {
    }

    constructor(title: String?, items: ArrayList<TocItem>) {
        this.title = title
        this.items = items
    }
}
