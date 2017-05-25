package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TOC item.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TocItem {
    
    private String title;
    private String url;
    private List<Article> articles;
}
