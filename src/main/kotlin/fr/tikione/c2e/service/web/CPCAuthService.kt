package fr.tikione.c2e.service.web

import fr.tikione.c2e.model.web.Auth

/**
 * Tools to authenticate on CanardPC website.
 */
interface CPCAuthService {

    /**
     * Authenticate on CanardPC website.
     * @param username username.
     * @param password password.
     * @return authentication data, to be reused by future requests.
     */
    fun authenticate(username: String, password: String): Auth
}
