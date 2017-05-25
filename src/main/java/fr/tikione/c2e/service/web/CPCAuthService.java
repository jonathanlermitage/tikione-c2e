package fr.tikione.c2e.service.web;

import fr.tikione.c2e.model.web.Auth;

/**
 * Tools to authenticate on CanardPC website.
 */
public interface CPCAuthService {
    
    /**
     * Authenticate on CanardPC website.
     * @param username username.
     * @param password password.
     * @return authentication data, to be reused by future requests.
     */
    Auth authenticate(String username, String password);
}
