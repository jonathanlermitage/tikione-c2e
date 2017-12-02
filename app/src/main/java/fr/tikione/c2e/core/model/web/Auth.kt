package fr.tikione.c2e.core.model.web

/**
 * Authentication data on CanardPC website.
 */
class Auth {

    var cookies: Map<String, String>? = null
    var login: String? = null
    var password: String? = null

    constructor() {
    }

    constructor(cookies: Map<String, String>?, login: String?, password: String?) {
        this.cookies = cookies
        this.login = login
        this.password = password
    }

    override fun toString(): String {
        return "Auth(cookies=$cookies, login=$login, password=$password)"
    }
}
