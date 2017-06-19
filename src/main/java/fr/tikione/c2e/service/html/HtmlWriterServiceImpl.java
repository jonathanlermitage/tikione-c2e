package fr.tikione.c2e.service.html;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.model.web.ArticleType;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.model.web.Picture;
import fr.tikione.c2e.model.web.TocCategory;
import fr.tikione.c2e.model.web.TocItem;
import lombok.SneakyThrows;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static fr.tikione.c2e.Main.VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.Normalizer.Form.NFD;

public class HtmlWriterServiceImpl implements HtmlWriterService {
    
    @Inject
    public HtmlWriterServiceImpl() {
    }
    
    private static final String EXT_LNK = "www";
    private final List<String> imgExt = Arrays.asList(".jpeg", ".jpg", ".png", ".tiff", ".gif");
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void write(Magazine magazine, File file, boolean incluePictures, boolean compressPictures) throws IOException {
        file.delete();
        if (file.exists()) {
            throw new IOException("impossible d'écraser le fichier : " + file.getAbsolutePath());
        }
        String faviconBase64 = resourceAsBase64("tmpl/html-export/img/french_duck.png");
        String fontRobotoBase64 = resourceAsBase64("tmpl/html-export/style/RobotoSlab-Light.ttf");
        String cssDay = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/style/day.css"), UTF_8)
                .replace("$$robotoFont_base64$$", fontRobotoBase64);
        String cssNight = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/style/night.css"), UTF_8);
        String js = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/main.js"), UTF_8);
        String header = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/header.html"), UTF_8)
                .replace("$$login$$", magazine.getLogin())
                .replace("$$version$$", VERSION)
                .replace("$$timestamp$$", new Date().toString())
                .replace("$$mag_number$$", Integer.toString(magazine.getNumber()))
                .replace("$$favicon_base64$$", faviconBase64)
                .replace("$$css_day$$", cssDay)
                .replace("$$css_night$$", cssNight)
                .replace("$$js$$", js);
        String footer = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("tmpl/html-export/footer.html"), UTF_8);
        
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
                            + normalizeAnchorUrl(category.getTitle() + tocItem.getTitle()) + "' "
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
                            + normalizeAnchorUrl(category.getTitle() + tocItem.getTitle())
                            + "'class=\"article-title\">"
                            + tocItem.getTitle() + "</div>\n\n");
                    tocItem.getArticles().forEach(article -> writeArticle(w, article, incluePictures, compressPictures));
                }
            }
            w.write("</div>\n");
            
            w.write(footer);
        }
        System.out.println("fichier HTML créé : " + file.getAbsolutePath());
    }
    
    private String resourceAsBase64(String path) throws IOException {
        return Base64.encodeBase64String(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(path)));
    }
    
    @SneakyThrows
    private void writeArticle(Writer w, Article article, boolean incluePictures, boolean compressPictures) {
        w.write("\n<!--article.getType()=" + article.getType() + "-->\n\n");
        if (ArticleType.NEWS == article.getType()) {
            w.write("<div class='news'>\n");
            if (filled(article.getCategory())) {
                w.write("<div class='news-category'>" + article.getCategory() + "</div>\n");
            }
            if (filled(article.getTitle())) {
                w.write("<div class='news-title'>" + article.getTitle() + "</div>\n");
            }
            writeArticleAuthorCreationdate(w, article);
            writeArticleContents(w, article);
        } else {
            w.write("<div class='article'>\n");
            writeArticleSpecs(w, article);
            writeArticleSubtitle(w, article);
            writeArticleHeaderContent(w, article);
            writeArticleAuthorCreationdate(w, article);
            writeArticleContents(w, article);
            if (incluePictures) {
                writeArticlePictures(w, article, compressPictures);
            }
            writeArticleOpinion(w, article);
            writeArticleState(w, article);
        }
        writeArticleLinks(w, article);
        w.write("</div>\n");
    }
    
    @SneakyThrows
    private void writeArticleSubtitle(Writer w, Article article) {
        if (filled(article.getSubtitle())) {
            w.write("<div class='article-subtitle'>");
            w.write(article.getSubtitle());
            w.write("</div>\n");
        }
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
    private void writeArticleHeaderContent(Writer w, Article article) {
        if (filled(article.getHeaderContent())) {
            w.write("<div class='article-headercontent'>" + article.getHeaderContent() + "</div>\n");
        }
    }
    
    @SneakyThrows
    private void writeArticleContents(Writer w, Article article) {
        for (String content : article.getContents()) {
            if (content != null && !content.isEmpty()) {
                String cssClass = "article-content";
                for (String encadre : article.getEncadreContents()) {
                    if (fastEquals(content, encadre)) {
                        cssClass = "article-encadre";
                        break;
                    }
                }
                w.write("<p class=\"" + cssClass + "\">" + content + "</p>\n");
            }
        }
    }
    
    @SneakyThrows
    private void writeArticleLinks(Writer w, Article article) {
        if (!article.getGameLinks().isEmpty()) {
            String lnksTitle = filled(article.getGameLinkTitle()) ? article.getGameLinkTitle() : "Liens externes";
            w.write("<div class='article-gamelink-title'>" + lnksTitle + "</div>\n");
            for (String lnk : article.getGameLinks()) {
                String lnkText = lnk.replaceAll("http://", "").replaceAll("https://", "");
                if (lnkText.length() > 50) {
                    lnkText = lnkText.substring(0, 47) + "...";
                }
                w.write("<div class='article-gamelink-title-lnk'><a class='article-gamelink-title-lnk' target='_blank' href='" + lnk + "'>" + lnkText + "</a></div>\n");
            }
        }
    }
    
    @SneakyThrows
    private void writeArticlePictures(Writer w, Article article, boolean compressPictures) {
        boolean hasPictures = false;
        for (Picture picture : article.getPictures()) {
            if (picture.getUrl() != null && !picture.getUrl().trim().isEmpty()) {
                hasPictures = true;
                break;
            }
        }
        if (hasPictures) {
            w.write("<div class='article-pictures'>\n");
            w.write("<div class='article-pictures-tip'>Images : cliquez/tapez sur une image pour l'agrandir, recommencez pour la réduire.</div>\n");
            for (Picture picture : article.getPictures()) {
                if (picture != null && picture.getUrl() != null && !picture.getUrl().isEmpty()) {
                    System.out.print("récupération de l'image " + picture.getUrl());
                    byte[] picBytes = IOUtils.toByteArray(new URL(picture.getUrl()));
                    System.out.println(" ok");
                    MagicMatch magicmatch = Magic.getMagicMatch(picBytes);
                    String ext = magicmatch.getExtension();
                    boolean isConvertible = false;
                    
                    if (compressPictures) {
                        // images with url that doesn't end with extension seems to use features unavailable in JPEG format: keep them
                        for (String anImgExt : imgExt) {
                            if (picture.getUrl().toLowerCase().endsWith(anImgExt)) {
                                isConvertible = true;
                                break;
                            }
                        }
                    }
                    
                    // use JPEG images only to reduce size of resulting HTML file
                    if (compressPictures && isConvertible && !"JPG".equalsIgnoreCase(ext) && !"JPEG".equalsIgnoreCase(ext)) {
                        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(picBytes));
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            ImageIO.write(bufferedImage, "JPG", baos);
                            picBytes = baos.toByteArray();
                        }
                        ext = "jpeg";
                    }
                    
                    String picB64 = Base64.encodeBase64String(picBytes);
                    String pictureId = Base64.encodeBase64String(picture.getUrl().getBytes())
                            .replaceAll("=", "")
                            .replaceAll(",", "");
                    String pictureBoxId = pictureId + "box";
                    w.write("<div class='article-picture-box' id='" + pictureBoxId + "'>\n");
                    w.write("<span class='article-picture'><img src='data:image/" + ext + ";base64," + picB64 +
                            "' id='" + pictureId + "' " +
                            " onclick=\"showPicture('" + pictureId + "', '" + pictureBoxId + "');\" /></span>\n");
                    if (picture.getLegend() != null && !picture.getLegend().isEmpty()) {
                        w.write("<span class='article-picture-legend'>" + picture.getLegend() + "</span>");
                    }
                    w.write("</div>\n");
                }
            }
            w.write("</div>\n");
        }
    }
    
    @SneakyThrows
    private void writeArticleOpinion(Writer w, Article article) {
        StringBuilder buff = new StringBuilder();
        buff.append("<div class='article-opinion'>\n");
        boolean contentFilled = false;
        if (filled(article.getGameOpinionTitle())) {
            buff.append("<div class='article-opinion-title'>").append(article.getGameOpinionTitle()).append("</div>\n");
            contentFilled = true;
        }
        if (filled(article.getGameOpinion())) {
            buff.append("<div class='article-opinion-content'>").append(article.getGameOpinion()).append("</div>\n");
            contentFilled = true;
        }
        if (filled(article.getGameScoreText())) {
            buff.append("<div class='article-opinion-score-text'>").append(article.getGameScoreText()).append("</span></div>\n");
            contentFilled = true;
        }
        if (filled(article.getGameScore())) {
            buff.append("<div class='article-opinion-score'>\n");
            if (filled(article.getGameScore())) {
                String score = article.getGameScore();
                if (score.startsWith("0.")) {
                    score = score.substring(2);
                }
                buff.append("<span class='article-opinion-score-number'>").append(score).append("</span>");
            }
            buff.append("</div>\n");
            contentFilled = true;
        }
        buff.append("</div>\n");
        if (contentFilled) {
            w.write(buff.toString());
        }
    }
    
    @SneakyThrows
    private void writeArticleState(Writer w, Article article) {
        StringBuilder buff = new StringBuilder();
        buff.append("<div class='article-state'>\n");
        boolean contentFilled = false;
        if (filled(article.getGameStateTitle())) {
            buff.append("<div class='article-state-title'>").append(article.getGameStateTitle()).append("</div>\n");
            contentFilled = true;
        }
        if (filled(article.getGameState())) {
            buff.append("<div class='article-state-content'>").append(article.getGameState()).append("</div>\n");
            contentFilled = true;
        }
        if (filled(article.getGameAdviceTitle()) && filled(article.getGameAdvice())) {
            buff.append("<div class='article-state-score-value'>").append(article.getGameAdviceTitle()).append(" ")
                    .append(article.getGameAdvice()).append("</span></div>\n");
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
    
    private String normalizeAnchorUrl(String str) {
        return Normalizer.normalize(str, NFD)
                .replaceAll("\"", "")
                .replaceAll("'", "")
                .replaceAll("\\s", "");
    }
    
    private boolean fastEquals(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        } else if (s1.length() == 0) {
            return true;
        }
        int portionSize = s1.length() > 30 ? 30 : s1.length();
        return s1.substring(0, portionSize).equals(s2.substring(0, portionSize));
    }
}
