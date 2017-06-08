package fr.tikione.c2e.service.web.scrap;

import com.google.inject.Inject;
import fr.tikione.c2e.Main;
import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.model.web.Auth;
import fr.tikione.c2e.model.web.Edito;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.model.web.TocCategory;
import fr.tikione.c2e.model.web.TocItem;
import fr.tikione.c2e.service.web.AbstractReader;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPCReaderServiceImpl extends AbstractReader implements CPCReaderService {
    
    @Inject
    private CPCScraperService cpcScraperService;
    
    @Inject
    public CPCReaderServiceImpl() {
    }
    
    @Override
    @SneakyThrows
    public List<Integer> listDownloadableMagazines(Auth auth) {
        Document doc = Jsoup.connect(CPC_BASE_URL)
                .cookies(auth.getCookies())
                .userAgent(UA)
                .get();
        Elements archives = doc.getElementsByClass("archive");
        List<Integer> magNumers = new ArrayList<>();
        archives.forEach(element -> magNumers.add(Integer.parseInt(element.getElementsByTag("a").attr("href").substring("/numero/".length()))));
        magNumers.add(magNumers.get(0) + 1);
        magNumers.sort(Comparator.reverseOrder());
        return magNumers;
    }
    
    @Override
    @SneakyThrows
    public Magazine downloadMagazine(Auth auth, int number) {
        System.out.println("téléchargement du magazine CPC " + number + "...");
        Document doc = queryUrl(auth, CPC_MAG_NUMBER_BASE_URL.replace("_NUM_", Integer.toString(number)));
        Magazine mag = new Magazine();
        mag.setNumber(number);
        mag.setTitle(doc.getElementById("numero-titre").text());
        mag.setLogin(auth.getLogin());
        mag.setEdito(extractEdito(doc));
        mag.setToc(extractToc(auth, doc));
        return mag;
    }
    
    private Edito extractEdito(Document doc) {
        Edito edito = new Edito();
        Element container = doc.getElementById("block-edito-content");
        edito.setAuthorAndDate(container.getElementById("numero-auteur-date").text());
        edito.setTitle(container.getElementById("numero-titre").text());
        edito.setContent(container.getElementById("numero-edito").text());
        return edito;
    }
    
    private List<TocCategory> extractToc(Auth auth, Document doc) {
        List<TocCategory> toc = new ArrayList<>();
        Element container = doc.getElementById("block-numerosommaire");
        Elements columns = container.getElementsByClass("columns");
        for (Element element : columns) {
            toc.add(buildTocItem(auth, element));
        }
        return toc;
    }
    
    private TocCategory buildTocItem(Auth auth, Element elt) {
        TocCategory tocCategory = new TocCategory();
        String title = clean(elt.getElementsByTag("h3").get(0).text());
        tocCategory.setTitle(title);
        elt.getElementsByTag("article").forEach(sheet -> tocCategory.getItems().add(new TocItem(
                sheet.text(),
                CPC_BASE_URL + attr(sheet.getElementsByTag("a"), "href"),
                extractArticles(auth, CPC_BASE_URL + attr(sheet.getElementsByTag("a"), "href"))))
        );
        return tocCategory;
    }
    
    @SneakyThrows
    private List<Article> extractArticles(Auth auth, String url) {
        System.out.print("récupération de l'article " + url);
        Document doc = queryUrl(auth, url);
        Map<Integer, List<Article>> articles = new HashMap<>();
        articles.putAll(cpcScraperService.extractArticles(doc));
        
        List<Article> res = articles.get(articles.keySet().stream().mapToInt(i -> i).max().orElseThrow(Exception::new));
        if (Main.DEBUG) {
            res.forEach(article -> System.out.println("\n" + article.toString(50)));
        }
        System.out.println(" ok");
        return res;
    }
}
