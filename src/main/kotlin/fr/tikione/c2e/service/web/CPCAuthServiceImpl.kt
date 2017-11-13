package fr.tikione.c2e.service.web

import fr.tikione.c2e.model.web.Auth
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
        
        log.debug("authenticate user by connecting to: {} ", CPC_LOGIN_FORM_POST_URL)
        
        loginForm = Jsoup.connect(CPC_LOGIN_FORM_POST_URL)
                .data("form_build_id", formBuildId)
                .data("form_id", "user_login_form")
                .data("name", username)
                .data("pass", password)
                .method(Connection.Method.POST)
                .userAgent(AbstractReader.UA + " with username '" + username + "'")
                .execute()
        if (200 != loginForm.statusCode()) {
            throw IOException("login failed: status code is " + loginForm.statusCode())
        }
        if (loginForm.cookies().isEmpty()) {
            throw IOException("login failed: no cookies")
        }
        loginForm.parse().body().getElementsByClass("user-logged") ?: throw IOException("login failed: can't find username in page header")
        return Auth(loginForm.cookies(), username, password)
    }
}
