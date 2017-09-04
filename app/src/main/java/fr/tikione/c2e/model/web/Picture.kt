package fr.tikione.c2e.model.web

/**
 * Picture url and its optional legend.
 */
class Picture {

    var url: String? = null
    var legend: String? = null

    constructor() {
    }

    constructor(url: String?, legend: String?) {
        this.url = url
        this.legend = legend
    }

    override fun toString(): String {
        return "Picture(url=$url, legend=$legend)"
    }
}
