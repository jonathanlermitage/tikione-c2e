package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * CanardPC web.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Magazine {
    
    private int number;
    private String title;
    private String login;
    
    private Edito edito;
    private List<TocCategory> toc = new ArrayList<>();
}
