package fr.tikione.c2e.service.html;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.model.web.TocCategory;
import fr.tikione.c2e.model.web.TocItem;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;

import static fr.tikione.c2e.Main.VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFD;

public class HtmlWriterServiceImpl implements HtmlWriterService {
    
    @Inject
    public HtmlWriterServiceImpl() {
    }
    
    private static final String EXT_LNK = "www";
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void write(Magazine magazine, File file, boolean incluePictures) throws IOException {
        file.delete();
        if (file.exists()) {
            throw new IOException("cannot write over file " + file.getAbsolutePath());
        }
        String faviconBase64 = resourceAsBase64("tmpl/html-export/ico/french_duck.png");
        String fontRobotoBase64 = resourceAsBase64("tmpl/html-export/font/RobotoSlab-Light.ttf");
        String header = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/header.html"), UTF_8)
                .replace("$$login$$", magazine.getLogin())
                .replace("$$version$$", VERSION)
                .replace("$$timestamp$$", new Date().toString())
                .replace("$$mag_number$$", Integer.toString(magazine.getNumber()))
                .replace("$$favicon_base64$$", faviconBase64)
                .replace("$$robotoFont_base64$$", fontRobotoBase64);
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
                            + Normalizer.normalize(category.getTitle() + tocItem.getTitle(), NFD) + "' "
                            + " onclick='showToc(false);'>" + tocItem.getTitle() + "</a> "
                            + "<a class='toc-ext-lnk' href='" + tocItem.getUrl() + "' target='_blank' title='Vers le site CanardPC - nouvelle page'>"
                            + EXT_LNK
                            + "</a></h3>\n\n");
                }
            }
            w.write("</div>\n");
            w.write("<br/><br/><br/></div>\n");
            w.write("<div id='articles'>\n");
    
            // articles
            for (TocCategory category : magazine.getToc()) {
                w.write("<h2 class=\"category-title\">" + category.getTitle() + "</h2>\n\n");
                for (TocItem tocItem : category.getItems()) {
                    w.write("<div id='"
                            + Normalizer.normalize(category.getTitle() + tocItem.getTitle(), NFD)
                            + "'class=\"article-title\">"
                            + tocItem.getTitle() + "</div>\n\n");
                    tocItem.getArticles().forEach(article -> writeArticle(w, article));
                }
            }
            w.write("</div>\n");
            
            w.write(footer);
        }
    }
    
    private String resourceAsBase64(String path) throws IOException {
        return Base64.encodeBase64String(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(path)));
    }
    
    @SneakyThrows
    private void writeArticle(Writer w, Article article) {
        w.write("<div class='article'>\n");
        writeArticleAuthorCreationdate(w, article);
        writeArticleSpecs(w, article);
        writeArticleContents(w, article);
        writeArticleOpinion(w, article);
        w.write("</div>\n");
    }
    
    @SneakyThrows
    private void writeArticleAuthorCreationdate(Writer w, Article article) {
        if (filled(article.getAuthorAndDate())) {
            w.write("<div class='article-author-creationdate'>");
            w.write(article.getAuthorAndDate());
            w.write("</div>\n");
        }
    }
    
    @SneakyThrows
    private void writeArticleSpecs(Writer w, Article article) {
        StringBuilder buff = new StringBuilder();
        buff.append("<div class='article-specs'>\n");
        boolean contentFilled = false;
        for (String spec : Arrays.asList(
                article.getGameDev(),
                article.getGameNature(),
                article.getGameEditor(),
                article.getGamePlatform(),
                article.getGameTester(),
                article.getGameConfig(),
                article.getGameDDL(),
                article.getGameLang(),
                article.getGameDRM())) {
            if (filled(spec)) {
                buff.append("<span class='article-specs-item'>// ").append(boldSpecTitle(spec)).append("</span> \n");
                contentFilled = true;
            }
        }
        buff.append("</div>\n");
        if (contentFilled) {
            w.write(buff.toString());
        }
    }
    
    @SneakyThrows
    private void writeArticleContents(Writer w, Article article) {
        for (String content : article.getContents()) {
            if (content != null && !content.isEmpty()) {
                w.write("<p class=\"article-content\">" + content + "</p>\n");
            }
        }
    }
    
    @SneakyThrows
    private void writeArticleOpinion(Writer w, Article article) {
        StringBuilder buff = new StringBuilder();
        buff.append("<div class='article-opinion'>\n");
        boolean contentFilled = false;
        if (filled(article.getGameOpinionTitle())) {
            buff.append("<div class='article-opinion-title'>").append(article.getGameOpinionTitle()).append("</div>");
            contentFilled = true;
        }
        if (filled(article.getGameOpinion())) {
            buff.append("<div class='article-opinion-content'>").append(article.getGameOpinion()).append("</div>");
            contentFilled = true;
        }
        if (filled(article.getGameScore()) || filled(article.getGameScoreText())) {
            buff.append("<div class='article-opinion-score'>");
            if (filled(article.getGameScore())) {
                String score = article.getGameScore();
                if (score.startsWith("0.")) {
                    score = score.substring(2);
                }
                buff.append("<span class='article-opinion-score-number'>").append(score).append("</span>");
            }
            if (filled(article.getGameScoreText())) {
                buff.append("<span class='article-opinion-score-text'>").append(article.getGameScoreText()).append("</span>");
            }
            buff.append("</div>");
            contentFilled = true;
        }
        buff.append("</div>\n");
        if (contentFilled) {
            w.write(buff.toString());
        }
    }
    
    private String boldSpecTitle(String str) {
        return str.contains(":") ? "<strong>" + str.substring(0, str.indexOf(":")) + "</strong> : " + str.substring(1 + str.indexOf(":")) : str;
    }
    
    private boolean filled(String str) {
        return str != null && !str.isEmpty();
    }
}
