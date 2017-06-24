package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Article paragraph.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paragraph {
    
    /** Raw text. */
    private String text;
    
    /** Some parts should be displayed in bold. */
    private List<StartEndIdx> bold = new ArrayList<>();
    
    /** Some parts should be displayed in italic. */
    private List<StartEndIdx> italic = new ArrayList<>();
    
    public Paragraph(String text) {
        this.text = text;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class StartEndIdx {
        private int start;
        private int end;
    }
}
