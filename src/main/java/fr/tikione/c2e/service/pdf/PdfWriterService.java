package fr.tikione.c2e.service.pdf;

import fr.tikione.c2e.model.web.Magazine;

import java.io.File;
import java.io.IOException;

public interface PdfWriterService {
    
    /**
     * Write a magazine to a new PDF file.
     * @param magazine magazine.
     * @param file PDF file.
     * @param incluePictures should include magazine pictures in PDF file?
     */
    void write(Magazine magazine, File file, boolean incluePictures) throws IOException;
}
