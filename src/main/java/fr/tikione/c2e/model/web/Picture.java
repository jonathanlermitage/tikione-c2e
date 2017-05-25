package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Picture url and its optional legend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {
    
    private String url;
    private String legend;
}
