package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Article paragraph.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paragraph {
    
    /** Raw text. */
    private String text;
}
