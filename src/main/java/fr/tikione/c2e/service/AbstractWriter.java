package fr.tikione.c2e.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.text.Normalizer;

import static fr.tikione.c2e.service.web.AbstractReader.CUSTOMTAG_EM_END;
import static fr.tikione.c2e.service.web.AbstractReader.CUSTOMTAG_EM_START;
import static fr.tikione.c2e.service.web.AbstractReader.CUSTOMTAG_STRONG_END;
import static fr.tikione.c2e.service.web.AbstractReader.CUSTOMTAG_STRONG_START;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFD;

public abstract class AbstractWriter {
    
    public static final String EXT_LNK = "www";
    
    public boolean filled(String str) {
        return str != null && !str.isEmpty();
    }
    
    public String normalizeAnchorUrl(String str) {
        return Normalizer.normalize(str, NFD)
                .replaceAll("\"", "")
                .replaceAll("'", "")
                .replaceAll("\\s", "");
    }
    
    public boolean fastEquals(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        } else if (s1.length() == 0) {
            return true;
        }
        int portionSize = s1.length() > 30 ? 30 : s1.length();
        return s1.substring(0, portionSize).equals(s2.substring(0, portionSize));
    }
    
    public String resourceAsBase64(String path)
            throws IOException {
        return Base64.encodeBase64String(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(path)));
    }
    
    public String resourceAsStr(String path)
            throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), UTF_8);
    }
    
    public String richToHtml(String rich) {
        return rich.replaceAll(CUSTOMTAG_EM_START, " <em> ")
                .replaceAll(CUSTOMTAG_EM_END, " </em> ")
                .replaceAll(CUSTOMTAG_STRONG_START, " <strong> ")
                .replaceAll(CUSTOMTAG_STRONG_END, " </strong> ");
    }
}
