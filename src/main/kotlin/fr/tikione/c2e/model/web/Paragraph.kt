package fr.tikione.c2e.model.web

/**
 * Article paragraph.
 */
class Paragraph {

    /** Raw text.  */
    var text: String? = null

    constructor() {
    }

    constructor(text: String?) {
        this.text = text
    }
}
