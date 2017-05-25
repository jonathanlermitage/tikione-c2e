package fr.tikione.c2e.service.web;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Auth;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class CPCAuthServiceImpl extends AbstractReader implements CPCAuthService {
    
    @Inject
    public CPCAuthServiceImpl() {
    }
    
    @Override
    @SneakyThrows
    public Auth authenticate(String username, String password) {
        Connection.Response loginForm = Jsoup.connect(CPC_LOGIN_FORM_POST_URL)
                .method(Connection.Method.GET)
                .userAgent(UA)
                .execute();
        String welcomeBody = loginForm.body();
        String formBuildStr = "name=\"form_build_id\" value=\"";
        String formBuildId = welcomeBody.substring(welcomeBody.indexOf(formBuildStr) + formBuildStr.length());
        formBuildId = formBuildId.substring(0, formBuildId.indexOf("\""));
        loginForm = Jsoup.connect(CPC_LOGIN_FORM_POST_URL)
                .data("form_build_id", formBuildId)
                .data("form_id", "user_login_form")
                .data("name", username)
                .data("pass", password)
                .method(Connection.Method.POST)
                .userAgent(UA + " with username '" + username + "'")
                .execute();
        if (200 != loginForm.statusCode()) {
            throw new IOException("login failed: status code is " + loginForm.statusCode());
        }
        if (loginForm.cookies().isEmpty()) {
            throw new IOException("login failed: no cookies");
        }
        if (!loginForm.parse().title().toUpperCase().contains(username.toUpperCase())) {
            throw new IOException("login failed: can't find username in page title");
        }
        return new Auth(loginForm.cookies(), username, password);
    }
}
