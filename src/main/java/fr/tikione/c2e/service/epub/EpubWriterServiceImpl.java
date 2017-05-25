package fr.tikione.c2e.service.epub;

import com.google.inject.Inject;
import fr.tikione.c2e.model.web.Magazine;

import java.io.File;
import java.io.IOException;

public class EpubWriterServiceImpl implements EpubWriterService {
    
    @Inject
    public EpubWriterServiceImpl() {
    }
    
    /** See <a href="https://github.com/psiegman/epublib">https://github.com/psiegman/epublib</a>. */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void write(Magazine magazine, File file, boolean incluePictures) throws IOException {
        file.delete();
        if (file.exists()) {
            throw new IOException("cannot write over file " + file.getAbsolutePath());
        }
        
        
    }
}
