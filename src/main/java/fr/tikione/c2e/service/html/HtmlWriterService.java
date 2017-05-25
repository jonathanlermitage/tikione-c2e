package fr.tikione.c2e.service.html;

import fr.tikione.c2e.model.web.Magazine;

import java.io.File;
import java.io.IOException;

public interface HtmlWriterService {
    
    /**
     * Write a magazine to a new single HTML file.
     * @param magazine magazine.
     * @param file HTML file.
     * @param incluePictures should include magazine pictures in HTML file?
     */
    void write(Magazine magazine, File file, boolean incluePictures) throws IOException;
}
