package fr.tikione.c2e.service.web.scrap;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.model.web.Picture;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.tikione.c2e.model.web.ArticleType.NEWS;
import static fr.tikione.c2e.model.web.ArticleType.SINGLE_NEWS;
import static fr.tikione.c2e.model.web.ArticleType.TESTS;

public class CPCScraperServiceImpl extends AbstractScraper implements CPCScraperService {
    
    @Inject
    public CPCScraperServiceImpl() {
    }
    
    public List<Article> extractNews(Document doc) {
        List<Article> articles = new ArrayList<>();
        if (doc.getElementsByClass("article-wrapper") != null) {
            for (Element elt : doc.getElementsByClass("article-wrapper")) {
                Article article = new Article();
                article.setType(NEWS);
                article.setCategory(text(elt.getElementsByClass("categorie")));
                article.setTitle(text(elt.getElementsByTag("h4")));
                String content = text(elt.getElementsByClass("article-wrapper"));
                if (content != null && article.getTitle() != null && content.length() > article.getTitle().length() && article.getTitle().length() > 0) {
                    // title and content are in the same div: fix content by removing title
                    content = content.substring(content.indexOf(article.getTitle()) + article.getTitle().length()).trim();
                }
                article.getContents().add(content);
                if (elt.getElementsByClass("singleImage") != null) {
                    article.getPictures().add(new Picture(attr(CPC_BASE_URL, elt.getElementsByClass("singleImage"), "href"), null));
                }
                articles.add(article);
            }
        }
        return articles;
    }
    
    @Override
    public List<Article> extractSingleNews(Document doc) {
        Article article = new Article();
        article.setType(SINGLE_NEWS);
        article.setTitle(text(doc.getElementsByClass("article-title")));
        article.setAuthorAndDate(text(doc.getElementsByClass("article-author")));
        article.getContents().add(text(doc.getElementsByClass("article-body")));
        doc.getElementsByClass("article-images")
                .forEach(images -> images.getElementsByClass("article-image")
                        .forEach(image -> article.getPictures().add(new Picture(
                                attr(CPC_BASE_URL, image.getElementsByTag("a"), "href"),
                                text(image.getElementsByClass("article-image-legende"))
                        ))));
        return Collections.singletonList(article);
    }
    
    @Override
    public List<Article> extractTests(Document doc) {
        Article article = new Article();
        article.setType(TESTS);
        article.setTitle(text(doc.getElementsByClass("article-title")));
        article.setSubtitle(text(doc.getElementsByClass("article-subtitle")));
        article.setAuthorAndDate(text(doc.getElementsByClass("article-author")));
        article.setHeaderContent(text(doc.getElementsByClass("article-chapo")));
        doc.getElementsByClass("article-body")
                .forEach(element -> article.getContents().add(text(element)));
        doc.getElementsByAttributeValueMatching("class", "(article\\-intertitre$|article\\-encadre$)")
                .forEach(element -> article.getContents().add(text(element)));
        doc.getElementsByClass("article-encadre")
                .forEach(element -> article.getEncadreContents().add(text(element)));
        article.setGameScore(attr(doc.getElementById("article-note"), "data-pourcentage"));
        article.setGameScoreText(text(doc.getElementsByClass("article-note-prix")));
        article.setGameNature(text(doc.getElementsByClass("game-genre")));
        article.setGameDev(text(doc.getElementsByClass("game-dev")));
        article.setGameEditor(text(doc.getElementsByClass("game-editor")));
        article.setGamePlatform(text(doc.getElementsByClass("game-platform")));
        article.setGameTester(text(doc.getElementsByClass("game-tester")));
        article.setGameConfig(text(doc.getElementsByClass("game-config")));
        article.setGameDDL(text(doc.getElementsByClass("game-ddl")));
        article.setGameLang(text(doc.getElementsByClass("game-lang")));
        article.setGameDRM(text(doc.getElementsByClass("game-drm")));
        article.setGameOpinionTitle(text(doc.getElementsByClass("article-note-avis-titre")));
        article.setGameOpinion(text(doc.getElementsByClass("article-note-avis")));
        article.setGameAdviceTitle(text(doc.getElementsByClass("article-note-conseil-titre")));
        article.setGameAdvice(text(doc.getElementsByClass("article-note-conseil")));
        article.setGamePrice(text(doc.getElementsByClass("article-note-prix")));
        article.setGameStateTitle(text(doc.getElementsByClass("article-note-etat-titre")));
        article.setGameState(text(doc.getElementsByClass("article-note-etat")));
        article.setGameOpinion(text(doc.getElementsByClass("article-note-avis")));
        article.setGameLinkTitle(text(doc.getElementsByClass("article-lien-description")));
        article.setGameLink(text(doc.getElementsByClass("article-lien-lien")));
        doc.getElementsByClass("article-images")
                .forEach(images -> images.getElementsByClass("article-image")
                        .forEach(image -> article.getPictures().add(new Picture(
                                attr(CPC_BASE_URL, image.getElementsByTag("a"), "href"),
                                text(image.getElementsByClass("article-image-legende"))
                        ))));
        if (doc.getElementsByClass("article-encadre-wrapper") != null) {
            for (Element elt : doc.getElementsByClass("article-encadre-wrapper")) {
                Article wa = new Article();
                wa.setType(TESTS);
                wa.setTitle(text(elt.getElementsByClass("title")));
                wa.getPictures().add(new Picture(attr(CPC_BASE_URL, elt.getElementsByTag("a"), "href"), null));
                String content = text(elt.getElementsByClass("article-encadre"));
                if (wa.getTitle() != null) {
                    content = content.substring(wa.getTitle().length()).trim();
                }
                wa.getContents().add(content);
                article.getWrappedArticles().add(wa);
            }
        }
        return Collections.singletonList(article);
    }
}
