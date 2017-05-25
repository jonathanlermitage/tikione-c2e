package fr.tikione.c2e.service.epub;

import fr.tikione.c2e.model.web.Magazine;

import java.io.File;
import java.io.IOException;

public interface EpubWriterService {
    
    /**
     * Write a magazine to a new EPUB file.
     * @param magazine magazine.
     * @param file EPUB file.
     * @param incluePictures should include magazine pictures in EPUB file?
     */
    void write(Magazine magazine, File file, boolean incluePictures) throws IOException;
}
