package fr.tikione.c2e.service.web.scrap;

import fr.tikione.c2e.model.web.Article;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

public interface CPCScraperService {
    
    /** Extract web page articles and associate this operation to a general relevance score. */
    Map<Integer, List<Article>> extractArticles(Document doc);
    
    List<Article> extractSingleNews(Document doc);
    
    List<Article> extractNews(Document doc);
    
    List<Article> extractTests(Document doc);
    
    List<Article> extractShortTests(Document doc);
    
    List<Article> extractPlumePudding(Document doc);
    
    List<Article> extractComing(Document doc);
    
    List<Article> extractUnderConstruction(Document doc);
    
    List<Article> extractTechno(Document doc);
    
    List<Article> extractStudy(Document doc);
    
    List<Article> extractEverythingElse(Document doc);
}
