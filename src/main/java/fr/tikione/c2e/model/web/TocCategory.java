package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * TOC category that contains {@link TocItem} elements.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TocCategory {
    
    private String title;
    private List<TocItem> items = new ArrayList<>();
}
