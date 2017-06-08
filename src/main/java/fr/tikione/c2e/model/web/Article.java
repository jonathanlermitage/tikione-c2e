package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an article, a game test, news, etc.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    
    private String title;
    private String subtitle;
    private String authorAndDate;
    private String category;
    private String headerContent;
    private List<String> contents = new ArrayList<>();
    private List<String> encadreContents = new ArrayList<>();
    private List<Picture> pictures = new ArrayList<>();
    private List<Article> wrappedArticles = new ArrayList<>();
    private String gameScore;
    private String gameScoreText;
    private String gameNature;
    private String gameDev;
    private String gameEditor;
    private String gamePlatform;
    private String gameTester;
    private String gameConfig;
    private String gameDDL;
    private String gameLang;
    private String gameDRM;
    private String gameOpinionTitle;
    private String gameOpinion;
    private String gameLinkTitle;
    private String gameLink;
    private String gameAdviceTitle;
    private String gameAdvice;
    private String gamePrice;
    private String gameStateTitle;
    private String gameState;
    
    /** Scrapper used to fill this object. For debug purpose. */
    private ArticleType type;
    
    @SuppressWarnings("SameParameterValue")
    public String toString(int maxLen) {
        return "Article{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", authorAndDate='" + authorAndDate + '\'' +
                ", category='" + category + '\'' +
                ", headerContent='" + headerContent + '\'' +
                ", contents=" + contents.stream().map(s -> shorten(s, maxLen)).collect(Collectors.toList()) + " contents.size=" + contents.size() +
                ", encadreContents=" + encadreContents.stream().map(s -> shorten(s, maxLen)).collect(Collectors.toList()) + " contents.size=" + encadreContents.size() +
                ", pictures=" + pictures +
                ", wrappedArticles=" + wrappedArticles +
                ", gameScore='" + gameScore + '\'' +
                ", gameScoreText='" + gameScoreText + '\'' +
                ", gameNature='" + gameNature + '\'' +
                ", gameDev='" + gameDev + '\'' +
                ", gameEditor='" + gameEditor + '\'' +
                ", gamePlatform='" + gamePlatform + '\'' +
                ", gameTester='" + gameTester + '\'' +
                ", gameConfig='" + gameConfig + '\'' +
                ", gameDDL='" + gameDDL + '\'' +
                ", gameLang='" + gameLang + '\'' +
                ", gameDRM='" + gameDRM + '\'' +
                ", gameOpinionTitle='" + gameOpinionTitle + '\'' +
                ", gameOpinion='" + gameOpinion + '\'' +
                ", gameLinkTitle='" + gameLinkTitle + '\'' +
                ", gameLink='" + gameLink + '\'' +
                ", gameAdviceTitle='" + gameAdviceTitle + '\'' +
                ", gameAdvice='" + gameAdvice + '\'' +
                ", gamePrice='" + gamePrice + '\'' +
                ", gameStateTitle='" + gameStateTitle + '\'' +
                ", gameState='" + gameState + '\'' +
                ", type=" + type +
                ", rate=" + rate() + "}";
    }
    
    private String shorten(String str, int maxLen) {
        return "[" + (str == null ? null : str.length() > maxLen ? str.substring(0, maxLen) : str) + "]";
    }
    
    /**
     * Give a score to current object to estimate fidelity to real article.
     * Used to compare multiple extractions of the same article and keep the best one.
     * @return score.
     */
    public int rate() {
        return toString().length();
    }
}
