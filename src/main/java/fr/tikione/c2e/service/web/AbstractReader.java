package fr.tikione.c2e.service.web;

import fr.tikione.c2e.model.web.Auth;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractReader {
    
    public final String CPC_BASE_URL = "https://www.canardpc.com";
    public final String CPC_LOGIN_FORM_POST_URL = CPC_BASE_URL + "/user/login";
    public final String CPC_MAG_NUMBER_BASE_URL = CPC_BASE_URL + "/numero/_NUM_";
    
    /** UserAgent to include when scrapping CanardPC website. This is not mandatory, but a good practice. */
    public static final String UA = "fr.tikione.c2e";
    
    /** Replace unbreakable spaces by regular spaces, and apply trim. */
    public String clean(String str) {
        while (str.contains(String.valueOf((char) 160))) {
            str = str.replace(String.valueOf((char) 160), " ");
        }
        return Jsoup.clean(str.trim(), Whitelist.none());
    }
    
    /** Get a remote document. */
    public Document queryUrl(Auth auth, String url) throws IOException {
        return Jsoup.connect(url)
                .cookies(auth.getCookies())
                .userAgent(UA)
                .get();
    }
    
    public String text(Element elt) {
        return elt == null ? null : clean((elt.text()));
    }
    
    public String text(Elements... elt) {
        if (elt == null || elt.length == 0) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        Arrays.stream(elt).forEach(elements -> buff.append(elements == null ? "" : clean(elements.text())));
        return buff.toString();
    }
    
    public String attr(Element elt, String attr) {
        return elt == null ? null : elt.attr(attr);
    }
    
    public String attr(Elements elt, String attr) {
        return elt == null ? null : elt.attr(attr);
    }
    
    public String attr(String baseUrl, Elements elt, String attr) {
        return elt == null || elt.attr(attr) == null || elt.attr(attr).trim().isEmpty() ? null : baseUrl + elt.attr(attr);
    }
}
