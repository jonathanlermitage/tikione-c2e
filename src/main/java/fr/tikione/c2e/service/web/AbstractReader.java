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
    public static final String CUSTOMTAG_EM_START = "__c2e_emStart__";
    public static final String CUSTOMTAG_EM_END = "__c2e_emEnd__";
    public static final String CUSTOMTAG_STRONG_START = "__c2e_strongStart__";
    public static final String CUSTOMTAG_STRONG_END = "__c2e_strongEnd__";
    private static final String EM_START = "<em>";
    private static final String EM_END = "</em>";
    private static final String STRONG_START = "<span class=\"title\">";
    private static final String STRONG_END = "</span>";
    
    /** UserAgent to include when scrapping CanardPC website. This is not mandatory, but a good practice. */
    public static final String UA = "fr.tikione.c2e";
    
    /** Replace unbreakable spaces by regular spaces, and apply trim. */
    public String clean(String str) {
        while (str.contains(String.valueOf((char) 160))) {
            str = str.replace(String.valueOf((char) 160), " ");
        }
        String res = Jsoup.clean(str.trim(), Whitelist.none());
        if (res.toUpperCase().endsWith("TWITTER FACEBOOK EMAIL")) {
            res = res.substring(0, res.toUpperCase().lastIndexOf("TWITTER FACEBOOK EMAIL")); // remove social buttons
        }
        return res.trim();
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
    
    /** Get text and keep some HTML styling tags: {@code em} and {@code strong}. */
    public String richText(Element elt) {
        if (elt == null) {
            return null;
        }
        String text = elt.html();
        int idx = 0;
        while (text.indexOf(EM_START, idx) >= 0) {
            idx = text.indexOf(EM_START, idx) + EM_START.length();
            int idxEnd = text.indexOf(EM_END, idx) + EM_END.length();
            if (idx < idxEnd) {
                text = text.substring(0, idx) + CUSTOMTAG_EM_START + text.substring(idx, idxEnd) + CUSTOMTAG_EM_END + text.substring(idxEnd);
            }
        }
        idx = 0;
        while (text.indexOf(STRONG_START, idx) >= 0) {
            idx = text.indexOf(STRONG_START, idx) + STRONG_START.length();
            int idxEnd = text.indexOf(STRONG_END, idx) + STRONG_END.length();
            if (idx < idxEnd) {
                text = text.substring(0, idx) + CUSTOMTAG_STRONG_START + text.substring(idx, idxEnd) + CUSTOMTAG_STRONG_END + text.substring(idxEnd);
            }
        }
        return clean(text);
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
