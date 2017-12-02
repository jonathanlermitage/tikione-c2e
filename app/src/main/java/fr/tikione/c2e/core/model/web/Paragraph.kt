package fr.tikione.c2e.core.model.web

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

    override fun toString(): String {
        return "Paragraph(text=$text)"
    }
}
