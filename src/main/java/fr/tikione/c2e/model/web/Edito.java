package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Edito.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edito {
    
    private String title;
    private String authorAndDate;
    private String content;
}
