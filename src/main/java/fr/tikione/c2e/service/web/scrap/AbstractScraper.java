package fr.tikione.c2e.service.web.scrap;

import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.service.web.AbstractReader;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractScraper extends AbstractReader implements CPCScraperService {
    
    public int rate(List<Article> articles) {
        return articles == null ? -1 : articles.stream().mapToInt(Article::rate).sum();
    }
    
    @Override
    @SneakyThrows
    public Map<Integer, List<Article>> extractArticles(Document doc) {
        Map<Integer, List<Article>> score = new HashMap<>();
        
        try {
            List<Article> news = extractNews(doc);
            score.put(rate(news), news);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> singleNews = extractSingleNews(doc);
            score.put(rate(singleNews), singleNews);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> tests = extractTests(doc);
            score.put(rate(tests), tests);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> shortTests = extractShortTests(doc);
            score.put(rate(shortTests), shortTests);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> plumePudding = extractPlumePudding(doc);
            score.put(rate(plumePudding), plumePudding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> coming = extractComing(doc);
            score.put(rate(coming), coming);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> underConstruction = extractUnderConstruction(doc);
            score.put(rate(underConstruction), underConstruction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> techno = extractTechno(doc);
            score.put(rate(techno), techno);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> study = extractStudy(doc);
            score.put(rate(study), study);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            List<Article> everythingElse = extractEverythingElse(doc);
            score.put(rate(everythingElse), everythingElse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return score;
    }
    
    @Override
    public List<Article> extractSingleNews(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractNews(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractTests(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractShortTests(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractPlumePudding(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractComing(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractUnderConstruction(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractTechno(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractStudy(Document doc) {
        return Collections.emptyList();
    }
    
    @Override
    public List<Article> extractEverythingElse(Document doc) {
        return Collections.emptyList();
    }
}
