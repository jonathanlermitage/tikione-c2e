package fr.tikione.c2e.service.html;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.model.web.TocCategory;
import fr.tikione.c2e.model.web.TocItem;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.Normalizer;
import java.util.Date;

import static fr.tikione.c2e.Main.VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFD;

public class HtmlWriterServiceImpl implements HtmlWriterService {
    
    @Inject
    public HtmlWriterServiceImpl() {
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void write(Magazine magazine, File file, boolean incluePictures) throws IOException {
        file.delete();
        if (file.exists()) {
            throw new IOException("cannot write over file " + file.getAbsolutePath());
        }
        String faviconBase64 = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/favicon-b64.txt"), UTF_8);
        String header = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/header.html"), UTF_8)
                .replace("$$login$$", magazine.getLogin())
                .replace("$$version$$", VERSION)
                .replace("$$timestamp$$", new Date().toString())
                .replace("$$mag_number$$", Integer.toString(magazine.getNumber()))
                .replace("$$favicon_base64$$", faviconBase64);
        String footer = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/footer.html"), UTF_8);
        
        // TODO escape HTML contents!!
        // TODO add a tiny PayPal donation link at end of document
        // TODO a JS/CSS selector to switch to night-mode
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write(header);
        
            // toc
            w.write("<div id='toc'>\n");
            w.write("<h1 class='toc-title'>Sommaire CanardPC n°" + magazine.getNumber() + "</h1>\n");
            w.write("<div class='toc-columns-container'>\n");
            for (TocCategory category : magazine.getToc()) {
                w.write("<h2 class='toc-category-title'>" + category.getTitle() + "</h2>\n\n");
                for (TocItem tocItem : category.getItems()) {
                    w.write("<h3 class='toc-item-title'><a href='#"
                            + Normalizer.normalize(category.getTitle() + tocItem.getTitle(), NFD)
                            + "' " +
                            " onclick='showToc(false);'>" + tocItem.getTitle() + "</a></h3>\n\n");
                }
            }
            w.write("</div>\n");
            w.write("</div>\n");
            w.write("<div id='articles'>\n");
        
            // articles
            for (TocCategory category : magazine.getToc()) {
                w.write("<h2 class=\"category-title\">" + category.getTitle() + "</h2>\n\n");
                for (TocItem tocItem : category.getItems()) {
                    w.write("<h3 id='"
                            + Normalizer.normalize(category.getTitle() + tocItem.getTitle(), NFD)
                            + "'class=\"toc-item-title\">"
                            + tocItem.getTitle() + "</h3>\n\n");
                    tocItem.getArticles().forEach(article -> writeArticle(w, article));
                }
            }
            w.write("</div>\n");
            
            w.write(footer);
        }
    }
    
    @SneakyThrows
    private void writeArticle(Writer w, Article article) {
        w.write("<h4 class=\"article-title\">" + article.getTitle() + "</h4>\n\n");
        for (String content : article.getContents()) {
            if (content != null && !content.trim().isEmpty()) {
                w.write("<div class=\"article-content\">" + content + "</div>\n\n");
            }
        }
        w.write("<hr>\n\n");
    }
}
