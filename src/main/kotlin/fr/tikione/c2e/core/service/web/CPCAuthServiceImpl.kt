package fr.tikione.c2e.core.service.web

import fr.tikione.c2e.core.model.web.Auth
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class CPCAuthServiceImpl : AbstractReader(), CPCAuthService {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun authenticate(username: String, password: String): Auth {
        var loginForm: Connection.Response = Jsoup.connect(CPC_LOGIN_FORM_POST_URL)
                .method(Connection.Method.GET)
                .userAgent(AbstractReader.UA)
                .execute()
        val welcomeBody = loginForm.body()
        val formBuildStr = "name=\"form_build_id\" value=\""
        var formBuildId = welcomeBody.substring(welcomeBody.indexOf(formBuildStr) + formBuildStr.length)
        formBuildId = formBuildId.substring(0, formBuildId.indexOf("\""))

        log.debug("authentification via : {} ", CPC_LOGIN_FORM_POST_URL)

        loginForm = Jsoup.connect(CPC_LOGIN_FORM_POST_URL)
                .data("form_build_id", formBuildId)
                .data("form_id", "user_login_form")
                .data("name", username)
                .data("pass", password)
                .method(Connection.Method.POST)
                .userAgent(AbstractReader.UA + " with username '" + username + "'")
                .execute()
        if (200 != loginForm.statusCode()) {
            throw IOException("echec d'authentification, le site semble ne pas repondre (code " + loginForm.statusCode() + ")")
        }
        if (loginForm.cookies().isEmpty()) {
            throw IOException("echec d'authentification : impossible de touver des cookies")
        }
        if (loginForm.parse().body().getElementsByClass("user-anonymous").isNotEmpty()) {
            throw IOException("echec d'authentification")
        }
        return Auth(loginForm.cookies(), username, password)
    }
}
