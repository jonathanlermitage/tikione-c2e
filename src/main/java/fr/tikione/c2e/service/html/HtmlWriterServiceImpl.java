package fr.tikione.c2e.service.html;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Article;
import fr.tikione.c2e.model.web.Magazine;
import fr.tikione.c2e.model.web.TocCategory;
import fr.tikione.c2e.model.web.TocItem;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import static fr.tikione.c2e.Main.VERSION;

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
        String css = "";
        String js = "";
        StringBuilder buff = new StringBuilder();
        buff.append("<!DOCTYPE html>\n");
        buff.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"fr\">\n");
        buff.append("<!-- ").append(new Date()).append(" -->\n");
        buff.append("<!-- File generated thanks to TikiOne C2E ").append(VERSION).append(" - https://github.com/jonathanlermitage/tikione-c2e -->\n");
        buff.append("<!-- This file is for private use, please do NOT share on DDL ou P2P networks -->\n");
        buff.append("<!-- Your CanardPC username is '").append(magazine.getLogin()).append("' -->\n");
        buff.append("<head>\n");
        buff.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n");
        buff.append("  <meta charset=\"utf-8\"/>\n");
        buff.append("  <title>").append("CPC n°").append(magazine.getNumber()).append("</title>\n");
        buff.append("  <style>\n").append(css).append("\n  </style>\n");
        buff.append("  <script>\n").append(js).append("\n  </script>\n");
        buff.append("</head>\n\n");
        buff.append("<body>\n\n");
        
        // TODO escape HTML contents!!
        // TODO add a tiny PayPal donation link at end of document
        // TODO a JS/CSS selector to switch to night-mode
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(buff.toString());
            for (TocCategory category : magazine.getToc()) {
                bw.write("<h2 class=\"category-title\">" + category.getTitle() + "</h2>\n\n");
                for (TocItem tocItem : category.getItems()) {
                    bw.write("<h3 class=\"toc-item-title\">" + category.getTitle() + "</h3>\n\n");
                    tocItem.getArticles().forEach(article -> writeArticle(bw, article));
                }
            }
            bw.write("</body>\n</html>\n");
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
